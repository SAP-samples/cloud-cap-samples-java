package my.bookshop.handlers;

import static com.sap.cds.services.cds.CqnService.EVENT_CREATE;
import static com.sap.cds.services.cds.CqnService.EVENT_UPDATE;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sap.cds.CdsData;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.services.ErrorStatuses;
import com.sap.cds.services.EventContext;
import com.sap.cds.services.ServiceException;
import com.sap.cds.services.changeset.ChangeSetContext;
import com.sap.cds.services.changeset.ChangeSetListener;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.messages.Messages;
import com.sap.cds.services.persistence.PersistenceService;

import cds.gen.Cuid;
import cds.gen.chapterservice.ChapterService_;
import cds.gen.chapterservice.Chapters;
import cds.gen.chapterservice.Chapters_;
import cds.gen.chapterservice.Pages;
import cds.gen.chapterservice.Pages_;

@Component
@ServiceName(ChapterService_.CDS_NAME)
public class ChapterServiceHandler implements EventHandler {

	private final PersistenceService db;
	private final Messages messages;

	private final Map<ChangeSetContext, Set<String>> chapterIds = new ConcurrentHashMap<>();
	private final Map<ChangeSetContext, Set<String>> pageIds = new ConcurrentHashMap<>();

	@Autowired
	public ChapterServiceHandler(PersistenceService db, Messages messages) {
		this.db = db;
		this.messages = messages;
	}

	@After(event = { EVENT_CREATE, EVENT_UPDATE }, entity = { Chapters_.CDS_NAME, Pages_.CDS_NAME })
	public void chapterOrPageChanged(EventContext context, Stream<CdsData> data) {
		data.map(d -> d.get(Cuid.ID).toString()).forEach(id -> markForValidation(context.getTarget().getQualifiedName(), id));
	}

	private void markForValidation(String entity, String id) {
		ensureListener();
		Map<ChangeSetContext, Set<String>> idMap = entity.equals(Chapters_.CDS_NAME) ? chapterIds : pageIds;
		idMap.get(ChangeSetContext.getCurrent()).add(id);
	}

	private void ensureListener() {
		ChangeSetContext context = ChangeSetContext.getCurrent();
		if(!chapterIds.containsKey(context) && !pageIds.containsKey(context)) {
			chapterIds.put(context, new HashSet<>());
			pageIds.put(context, new HashSet<>());
			context.register(new ChangeSetListener(){

				@Override
				public void beforeClose() {
					if(context.isMarkedForCancel()) {
						// do not overwrite the original error
						return;
					}

					Set<String> chapters = new HashSet<>(chapterIds.get(context));

					// map pages to chapters
					if(!pageIds.get(context).isEmpty()) {
						db.run(Select.from(Pages_.class)
							.columns(p -> p.chapter_ID())
							.where(p -> p.ID().in(new ArrayList<>(pageIds.get(context))))
							.distinct())
						.stream()
						.map(r -> (String) r.get(Pages.CHAPTER_ID))
						.forEach(chapters::add);
					}

					// map chapters to books
					if(!chapters.isEmpty()) {
						Set<String> books = db.run(Select.from(Chapters_.class)
							.columns(c -> c.book())
							.where(c -> c.ID().in(new ArrayList<>(chapters)))
							.distinct())
						.stream()
						.map(r -> (String) r.get(Chapters.BOOK))
						.collect(Collectors.toSet());

						books.forEach((b) -> validateBook(b));
					}

				}

				@Override
				public void afterClose(boolean completed) {
					chapterIds.remove(context);
					pageIds.remove(context);
				}

			});
		}
	}

	private void validateBook(String book) {
		List<Chapters> chapters = db.run(Select.from(Chapters_.class)
			.columns(c -> c._all(), c -> c.pages().expand().orderBy(p -> p.number().asc()))
			.where(c -> c.book().eq(book))
			.orderBy(c -> c.number().asc()))
			.listOf(Chapters.class);

		int lastChapterNo = 0;
		for(Chapters chapter: chapters) {
			if(chapter.getNumber() != ++lastChapterNo) {
				messages.error("Chapter numbers of book '{}' are not consistent at number {}", book, lastChapterNo);
			}

			long wordCount = chapter.getWordCount() != null ? chapter.getWordCount() : 0;
			if(chapter.getPages() != null && !chapter.getPages().isEmpty()) {
				int lastPageNo = 0;
				for(Pages page: chapter.getPages()) {
					if(page.getNumber() != ++lastPageNo) {
						messages.error("Page numbers of book '{}' chapter {} are not consistent at number {}", book, lastChapterNo, lastPageNo);
					}

					wordCount += page.getContent().length();
				}
			} else {
				messages.error("Chapter number {} of book '{}' does not have any pages", lastChapterNo, book);
			}
			chapter.setWordCount(wordCount);
		}

		if(messages.stream().count() > 0) {
			throw new ServiceException(ErrorStatuses.BAD_REQUEST, "The request validation failed");
		}

		// update word count in database
		db.run(Update.entity(Chapters_.class).entries(chapters));
	}

}
