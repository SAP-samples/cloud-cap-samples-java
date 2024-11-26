package my.bookshop.handlers;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.sap.cds.Row;
import com.sap.cds.ql.CQL;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.cqn.CqnElementRef;
import com.sap.cds.ql.cqn.CqnPredicate;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.ql.cqn.CqnValue;
import com.sap.cds.ql.cqn.Modifier;
import com.sap.cds.ql.cqn.transformation.CqnTopLevelsTransformation;
import com.sap.cds.ql.cqn.transformation.CqnAncestorsTransformation;
import com.sap.cds.ql.cqn.transformation.CqnDescendantsTransformation;
import com.sap.cds.ql.cqn.transformation.CqnFilterTransformation;
import com.sap.cds.ql.cqn.transformation.CqnSearchTransformation;
import com.sap.cds.ql.cqn.transformation.CqnOrderByTransformation;
import com.sap.cds.ql.cqn.transformation.CqnTransformation;
import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;

import cds.gen.adminservice.AdminService_;
import cds.gen.adminservice.GenreHierarchy;
import cds.gen.adminservice.GenreHierarchy_;

@Component
@Profile("default")
@ServiceName(AdminService_.CDS_NAME)
public class HierarchyHandler implements EventHandler {

    private final PersistenceService db;

    HierarchyHandler(PersistenceService db) {
		this.db = db;
	}

    @Before(event = CqnService.EVENT_READ, entity = GenreHierarchy_.CDS_NAME)
    public void readGenreHierarchy(CdsReadEventContext event) {
        List<CqnTransformation> trafos = event.getCqn().transformations();
        List<GenreHierarchy> result = null;

        if (trafos.size() < 1) {
            return;
        }
         
        if (getTopLevels(trafos) instanceof CqnTopLevelsTransformation topLevels) {
            result = topLevels(topLevels, CQL.TRUE);
        } else if (trafos.get(0) instanceof CqnDescendantsTransformation descendants) {
            result = handleDescendants(descendants);
        }  else if (trafos.get(0) instanceof CqnAncestorsTransformation ancestors) {
            if (trafos.size() == 2 && trafos.get(1) instanceof CqnTopLevelsTransformation topLevels) {
                result = handleAncestors(ancestors, topLevels);
            } else if (trafos.size() == 3 && trafos.get(2) instanceof CqnTopLevelsTransformation topLevels) {
                result = handleAncestors(ancestors, topLevels);
            }
        }

        setResult(event, result);
    }

    private CqnTopLevelsTransformation getTopLevels(List<CqnTransformation> trafos) {
        if (trafos.get(0) instanceof CqnTopLevelsTransformation topLevels) {
            return topLevels;
        } else if (trafos.size() == 2 && trafos.get(0) instanceof CqnOrderByTransformation && trafos.get(1) instanceof CqnTopLevelsTransformation topLevels) {
            return topLevels;
        }
        return null;
    }

    private void setResult(CdsReadEventContext event, List<GenreHierarchy> result) {
        if (!result.isEmpty()) {
            addDrillState(result);
        }

        event.setResult(result);
    }

    private void addDrillState(List<GenreHierarchy> ghs) {
        List<Integer> ids = ghs.stream().map(gh -> gh.getNodeId()).toList();
        Set<Integer> parents = ghs.stream().map(gh -> gh.getParentId()).filter(p -> p != 0).collect(Collectors.toSet());
        CqnSelect q = Select.from(AdminService_.GENRE_HIERARCHY, gh -> gh.parent()).columns(gh -> gh.node_id())
                .where(gh -> gh.node_id().in(ids));
        Set<Object> nonLeafs = db
                .run(q)
                .stream().map(r -> r.get(GenreHierarchy.NODE_ID)).collect(Collectors.toSet());

        for (GenreHierarchy gh : ghs) {
            Integer id = gh.getNodeId();
            if (nonLeafs.contains(id)) {
                if (parents.contains(id)) {
                    gh.setDrillState("expanded");
                } else {
                    gh.setDrillState("collapsed");
                }
            } else {
                gh.setDrillState("leaf");
            }
        } 
    }
        
    private List<GenreHierarchy> handleDescendants(CqnDescendantsTransformation descendants) {
        Map<Integer, GenreHierarchy> lookup = new HashMap<>();
        CqnFilterTransformation filter = (CqnFilterTransformation) descendants.transformations().get(0);
        CqnSelect getRoot = Select.from(AdminService_.GENRE_HIERARCHY).where(filter.filter());
        GenreHierarchy root = db.run(getRoot).single(GenreHierarchy.class);
        lookup.put(root.getNodeId(), root);

        CqnPredicate parentFilter = CQL.copy(filter.filter(), new Modifier() {
            @Override
            public CqnValue ref(CqnElementRef ref) {
                return CQL.get(GenreHierarchy.PARENT_ID);
            }
        });

        CqnSelect childrenCQN = Select.from(AdminService_.GENRE_HIERARCHY).where(parentFilter);
        List<GenreHierarchy> children = db.run(childrenCQN).listOf(GenreHierarchy.class);
        children.forEach(gh -> lookup.put(gh.getNodeId(), gh));
        children.forEach(gh -> gh.setParent(lookup.get(gh.getParentId())));

        return children.stream().sorted(new Sorter()).toList();
    }

    private List<GenreHierarchy> handleAncestors(CqnAncestorsTransformation ancestors, CqnTopLevelsTransformation topLevels) {
        CqnTransformation trafo = ancestors.transformations().get(0);
        Select<GenreHierarchy_> inner = Select.from(AdminService_.GENRE_HIERARCHY).columns(gh -> gh.node_id());
        if (trafo instanceof CqnFilterTransformation filter) {
            inner.where(filter.filter());
        } else if (trafo instanceof CqnSearchTransformation search) {
            inner.search(search.search());
        }
        Select<GenreHierarchy_> outer = Select.from(AdminService_.GENRE_HIERARCHY).columns(gh -> gh.node_id().as("i0"), gh -> gh.parent().node_id().as("i1"),
            gh -> gh.parent().parent().node_id().as("i2"), gh -> gh.parent().parent().parent().node_id().as("i3"),
            gh -> gh.parent().parent().parent().parent().node_id().as("i4")).where(gh -> gh.node_id().in(inner));

        Set<Integer> ancestorIds = new HashSet<>();
        db.run(outer).stream().forEach(r -> {
            addIfNotNull(ancestorIds, r, "i0");
            addIfNotNull(ancestorIds, r, "i1");
            addIfNotNull(ancestorIds, r, "i2");
            addIfNotNull(ancestorIds, r, "i3");
            addIfNotNull(ancestorIds, r, "i4");
        });

        CqnPredicate filter = CQL.get("node_id").in(ancestorIds.stream().toList());
        return topLevels(topLevels, filter);
    }

    private void addIfNotNull(Set<Integer> ancestorIds, Row r, String key) {
        Integer id = (Integer) r.get(key);
        if (id != null) {
            ancestorIds.add(id);
        }
    } 

    private List<GenreHierarchy> topLevels(CqnTopLevelsTransformation topLevels, CqnPredicate filter) {
        return topLevels.levels() < 0 || !(topLevels.expandLevels().isEmpty()) ? topLevelsAll(filter) : topLevelsLimit(topLevels.levels(), filter);
    }

    private List<GenreHierarchy> topLevelsLimit(long limit, CqnPredicate filter) {
        Map <Integer, GenreHierarchy> lookup = new HashMap<>();

        CqnSelect getRoots = Select.from(AdminService_.GENRE_HIERARCHY).where(gh -> gh.parent_id().eq(0).and(filter));
        List<GenreHierarchy> roots = db.run(getRoots).listOf(GenreHierarchy.class);
        roots.forEach(root -> {
            root.setDistanceFromRoot(0l);
            lookup.put(root.getNodeId(), root);
            List<Integer> parents = List.of(root.getNodeId());
            for (long i = 1; i < limit; i++) {
                List<Integer> ps = parents;
                CqnSelect getChildren = Select.from(AdminService_.GENRE_HIERARCHY).where(gh -> gh.parent_id().in(ps).and(filter));
                List<GenreHierarchy> children = db.run(getChildren).listOf(GenreHierarchy.class);
                if (children.isEmpty()) {
                    break;
                }
                long dfr = i;
                parents = children.stream().peek(gh -> {
                    gh.setParent(lookup.get(gh.getParentId()));
                    gh.setDistanceFromRoot(dfr);
                    lookup.put(gh.getNodeId(), gh);
                }).map(GenreHierarchy::getNodeId).toList();
            }
        });

        return lookup.values().stream().sorted(new Sorter()).toList();
    }

    private List<GenreHierarchy> topLevelsAll(CqnPredicate filter) {
        Map<Integer, GenreHierarchy> lookup = new HashMap<>();

        CqnSelect allCqn = Select.from(AdminService_.GENRE_HIERARCHY).where(filter);
        var all = db.run(allCqn).listOf(GenreHierarchy.class);
        all.forEach(gh -> lookup.put(gh.getNodeId(), gh));
        all.forEach(gh -> gh.setParent(lookup.get(gh.getParentId())));
        all.forEach(gh -> gh.setDistanceFromRoot(distanceFromRoot(gh)));

        return all.stream().sorted(new Sorter()).toList();
    }

    private static long distanceFromRoot(GenreHierarchy gh) {
        long dfr = 0;
        while (gh.getParent() != null) {
            dfr++;
            gh = gh.getParent();
        }

        return dfr;
    }

    private class Sorter implements Comparator<GenreHierarchy> {

        @Override
        public int compare(GenreHierarchy gh1, GenreHierarchy gh2) {
            Deque<String> path1 = getPath(gh1);
            Deque<String> path2 = getPath(gh2);
            int res = 0;

            while (!path1.isEmpty() && !path2.isEmpty()) {
                String last1 = path1.removeFirst();
                String last2 = path2.removeFirst();
                res = last1.compareTo(last2);
                if (res != 0) {
                    return res;
                }
            }
            return res;
        }

        Deque<String> getPath(GenreHierarchy gh){
            Deque<String> path = new ArrayDeque<>();
            do {
                path.addFirst(gh.getName());
                gh = gh.getParent();
            }  while (gh != null);

            return path;
        } 
    }
}
