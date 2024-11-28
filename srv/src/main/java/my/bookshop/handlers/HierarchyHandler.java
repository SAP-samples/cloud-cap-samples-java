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

import static cds.gen.adminservice.AdminService_.GENRE_HIERARCHY;

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
        } else if (trafos.get(0) instanceof CqnAncestorsTransformation ancestors) {
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
        } else if (trafos.size() == 2 && trafos.get(0) instanceof CqnOrderByTransformation
                && trafos.get(1) instanceof CqnTopLevelsTransformation topLevels) {
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
        List<Integer> ids = ghs.stream().map(gh -> gh.getId()).toList();
        Set<Integer> parents = ghs.stream().map(gh -> gh.getParntId()).filter(p -> p != null)
                .collect(Collectors.toSet());
        CqnSelect q = Select.from(GENRE_HIERARCHY).columns(gh -> gh.parnt_ID().as("id"))
                .where(gh -> gh.parnt_ID().in(ids));
        Set<Object> nonLeafs = db
                .run(q)
                .stream().map(r -> r.get("id")).collect(Collectors.toSet());

        for (GenreHierarchy gh : ghs) {
            Integer id = gh.getId();
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

    private CqnPredicate descendantsFilter(CqnDescendantsTransformation descendants) {
        CqnTransformation trafo = descendants.transformations().get(0);
        CqnPredicate start = ((CqnFilterTransformation) trafo).filter();
        CqnPredicate result = CQL.FALSE;
        if (descendants.keepStart()) {
            result = CQL.or(result, start);
        }
        CqnPredicate children = CQL.copy(start, new Modifier() {
            @Override
            public CqnValue ref(CqnElementRef ref) {
                return CQL.get(GenreHierarchy.PARNT_ID);
            }
        });
        result = CQL.or(result, children);

        return result;
    }

    private CqnPredicate ancestorsFilter(CqnAncestorsTransformation ancestors) {
        CqnTransformation trafo = ancestors.transformations().get(0);
        Select<GenreHierarchy_> inner = Select.from(GENRE_HIERARCHY).columns(gh -> gh.ID());
        if (trafo instanceof CqnFilterTransformation filter) {
            inner.where(filter.filter());
        } else if (trafo instanceof CqnSearchTransformation search) {
            inner.search(search.search());
        }

        Select<GenreHierarchy_> outer = Select.from(GENRE_HIERARCHY)
                .columns(gh -> gh.ID().as("i0"),
                        gh -> gh.parnt().ID().as("i1"),
                        gh -> gh.parnt().parnt().ID().as("i2"),
                        gh -> gh.parnt().parnt().parnt().ID().as("i3"),
                        gh -> gh.parnt().parnt().parnt().parnt().ID().as("i4"))
                .where(gh -> gh.ID().in(inner));

        Set<Integer> ancestorIds = new HashSet<>();
        db.run(outer).stream().forEach(r -> {
            addIfNotNull(ancestorIds, r, "i0");
            addIfNotNull(ancestorIds, r, "i1");
            addIfNotNull(ancestorIds, r, "i2");
            addIfNotNull(ancestorIds, r, "i3");
            addIfNotNull(ancestorIds, r, "i4");
        });

        return CQL.get(GenreHierarchy_.ID).in(ancestorIds.stream().toList());
    }

    private List<GenreHierarchy> handleDescendants(CqnDescendantsTransformation descendants) {
        CqnPredicate filter = descendantsFilter(descendants);
        CqnSelect childrenCQN = Select.from(GENRE_HIERARCHY).where(filter);
        List<GenreHierarchy> nodes = db.run(childrenCQN).listOf(GenreHierarchy.class);

        connect(nodes);

        return nodes.stream().sorted(new Sorter()).toList();
    }

    private static void connect(List<GenreHierarchy> nodes) {
        Map<Integer, GenreHierarchy> lookup = new HashMap<>();
        nodes.forEach(gh -> lookup.put(gh.getId(), gh));
        nodes.forEach(gh -> gh.setParnt(lookup.get(gh.getParntId())));
        nodes.forEach(gh -> gh.setDistanceFromRoot(distanceFromRoot(gh)));
    }

    private List<GenreHierarchy> handleAncestors(CqnAncestorsTransformation ancestors,
            CqnTopLevelsTransformation topLevels) {
        CqnPredicate filter = ancestorsFilter(ancestors);

        return topLevels(topLevels, filter);
    }

    private void addIfNotNull(Set<Integer> ancestorIds, Row r, String key) {
        Integer id = (Integer) r.get(key);
        if (id != null) {
            ancestorIds.add(id);
        }
    }

    private List<GenreHierarchy> topLevels(CqnTopLevelsTransformation topLevels, CqnPredicate filter) {
        return topLevels.levels() < 0 ? topLevelsAll(filter) : topLevelsLimit(topLevels, filter);
    }

    private List<GenreHierarchy> topLevelsLimit(CqnTopLevelsTransformation topLevels, CqnPredicate filter) {
        long limit = topLevels.levels();
        Map <Integer, GenreHierarchy> lookup = new HashMap<>();
        Map<Object, Long> expandLevels = topLevels.expandLevels();

        CqnSelect getRoots = Select.from(GENRE_HIERARCHY).where(gh -> gh.parnt_ID().isNull().and(filter));
        List<GenreHierarchy> roots = db.run(getRoots).listOf(GenreHierarchy.class);
        roots.forEach(root -> {
            root.setDistanceFromRoot(0l);
            lookup.put(root.getId(), root);
            List<Integer> parents = List.of(root.getId());
            for (long i = 1; i < limit; i++) {
                List<Integer> ps = parents;
                CqnSelect getChildren = Select.from(GENRE_HIERARCHY)
                        .where(gh -> gh.parnt_ID().in(ps).and(filter));
                List<GenreHierarchy> children = db.run(getChildren).listOf(GenreHierarchy.class);
                if (children.isEmpty()) {
                    break;
                }
                long dfr = i;
                parents = children.stream().peek(gh -> {
                    gh.setParnt(lookup.get(gh.getParntId()));
                    gh.setDistanceFromRoot(dfr);
                    lookup.put(gh.getId(), gh);
                }).map(GenreHierarchy::getId).toList();
            }
        });

        if (!expandLevels.isEmpty()) {
            List<Integer> expandedIds = expandLevels.keySet().stream().map(key -> (Integer) key).toList();
            CqnSelect expandedCQN = Select.from(AdminService_.GENRE_HIERARCHY).where(gh -> 
                    CQL.and(filter,
                    CQL.or(gh.ID().in(expandedIds), gh.parnt_ID().in(expandedIds))));
            
            List<GenreHierarchy> expanded = db.run(expandedCQN).listOf(GenreHierarchy.class);
            expanded.forEach(gh -> {
                if (!lookup.keySet().contains(gh.getId())) {
                    gh.setParnt(lookup.get(gh.getParntId()));
                    gh.setDistanceFromRoot(distanceFromRoot(gh));
                    lookup.put(gh.getId(), gh);
                }
            });

        }

        return lookup.values().stream().sorted(new Sorter()).toList();
    }

    private List<GenreHierarchy> topLevelsAll(CqnPredicate filter) {
        CqnSelect allCqn = Select.from(GENRE_HIERARCHY).where(filter);
        var all = db.run(allCqn).listOf(GenreHierarchy.class);

        connect(all);

        return all.stream().sorted(new Sorter()).toList();
    }

    private static long distanceFromRoot(GenreHierarchy gh) {
        long dfr = 0;
        while (gh.getParnt() != null) {
            dfr++;
            gh = gh.getParnt();
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
                String last1 = path1.pop();
                String last2 = path2.pop();
                res = last1.compareTo(last2);
                if (res != 0) {
                    return res;
                }
            }
            return res;
        }

        Deque<String> getPath(GenreHierarchy gh) {
            Deque<String> path = new ArrayDeque<>();
            do {
                path.push(gh.getName());
                gh = gh.getParnt();
            } while (gh != null);

            return path;
        }
    }
}
