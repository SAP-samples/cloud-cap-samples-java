package my.bookshop;

import static cds.gen.chapterservice.ChapterService_.CHAPTERS;
import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.apache.tomcat.util.http.fileupload.util.Streams;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.google.common.io.Resources;
import com.sap.cds.ql.Delete;
import com.sap.cds.services.persistence.PersistenceService;

import cds.gen.chapterservice.Chapters;
import cds.gen.chapterservice.Pages;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ChangeSetBasedValidationsITest {

	private static final String batchURI = "/api/ChapterService/$batch";
	private static final String chaptersURI = "/api/ChapterService/Chapters";
	private static final String chapterURI = "/api/ChapterService/Chapters(%s)";
	private static final String pageURI = "/api/ChapterService/Chapters(%s)/pages(%s)";


	private static final String batchURIV2 = batchURI.replace("/api", "/odata/v2");
	private static final String chaptersURIV2 = chaptersURI.replace("/api", "/odata/v2");
	private static final String chapterURIV2 = chapterURI.replace("/api", "/odata/v2");

	private static final Chapters chapter;
	private static final Chapters errorChapter;
	private static final Chapters errorChapterUpdate;
	private static final Pages errorPageUpdate;

	static {
		chapter = Chapters.create();
		chapter.setId("c6129637-98a5-4631-a7ba-b2e4cd04cf1d");
		chapter.setBook("The greatest Book of all");
		chapter.setNumber(1);
		chapter.setTitle("First chapter");

		Pages page1 = Pages.create();
		page1.setId("43656f84-410d-442a-80bb-7c94edb03de7");
		page1.setNumber(1);
		page1.setContent("What a great page!");

		Pages page2 = Pages.create();
		page2.setNumber(2);
		page2.setContent("The second is even better!");
		chapter.setPages(Arrays.asList(page1, page2));

		//
		errorChapter = Chapters.create();
		errorChapter.setBook("Not so great Book");
		errorChapter.setNumber(1);
		errorChapter.setTitle("First chapter");

		Pages errorPage1 = Pages.create();
		errorPage1.setNumber(1);
		errorPage1.setContent("The start is okay.");

		Pages errorPage3 = Pages.create();
		errorPage3.setNumber(3);
		errorPage3.setContent("The third is dangling.");
		errorChapter.setPages(Arrays.asList(errorPage1, errorPage3));

		//
		errorChapterUpdate = Chapters.create();
		errorChapterUpdate.setBook("The greatest Book of all");
		errorChapterUpdate.setNumber(2);
		errorChapterUpdate.setTitle("First chapter");

		//
		errorPageUpdate = Pages.create();
		errorPageUpdate.setNumber(100);
		errorPageUpdate.setContent("What a great page!");
	}

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private PersistenceService db;

	@AfterEach
	public void cleanup() {
		db.run(Delete.from(CHAPTERS));
	}

	@Test
	public void testDeepRequests() throws Exception {
		// deep insert
		mockMvc.perform(post(chaptersURI).contentType(MediaType.APPLICATION_JSON).content(chapter.toJson()))
				.andExpect(status().isCreated());

		// check word count
		mockMvc.perform(get(chaptersURI))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.value[0].wordCount").value(44));

		// deep insert with error
		mockMvc.perform(post(chaptersURI).contentType(MediaType.APPLICATION_JSON).content(errorChapter.toJson()))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error.details[0].message").value("Page numbers of book 'Not so great Book' chapter 1 are not consistent at number 2"));

		// chapter update with error
		mockMvc.perform(patch(String.format(chapterURI, chapter.getId())).contentType(MediaType.APPLICATION_JSON).content(errorChapterUpdate.toJson()))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error.details[0].message").value("Chapter numbers of book 'The greatest Book of all' are not consistent at number 1"));

		// page update with error
		mockMvc.perform(patch(String.format(pageURI, chapter.getId(), chapter.getPages().get(0).getId())).contentType(MediaType.APPLICATION_JSON).content(errorPageUpdate.toJson()))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error.details[0].message").value("Page numbers of book 'The greatest Book of all' chapter 1 are not consistent at number 1"))
				.andExpect(jsonPath("$.error.details[1].message").value("Page numbers of book 'The greatest Book of all' chapter 1 are not consistent at number 2"));
	}

	@Test
	public void testDeepRequestsV2() throws Exception {
		// deep insert
		mockMvc.perform(post(chaptersURIV2).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(chapter.toJson()))
				.andExpect(status().isCreated());

		// check word count
		mockMvc.perform(get(chaptersURIV2).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.d.results[0].wordCount").value(44));

		// deep insert with error
		mockMvc.perform(post(chaptersURIV2).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(errorChapter.toJson()))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error.innererror.errordetails[0].message").value("Page numbers of book 'Not so great Book' chapter 1 are not consistent at number 2"));

		mockMvc.perform(patch(String.format(chapterURIV2, "guid'" + chapter.getId() + "'")).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(errorChapterUpdate.toJson()))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error.innererror.errordetails[0].message").value("Chapter numbers of book 'The greatest Book of all' are not consistent at number 1"));
	}

	@Test
	public void testSuccessAndErrorBatches() throws Exception {
		String batchSuccess = Streams.asString(Resources.getResource("batchSuccess.txt").openStream());
		mockMvc.perform(post(batchURI).contentType("multipart/mixed; boundary=batch_1").content(batchSuccess))
				.andExpect(status().isOk());

		mockMvc.perform(get(chaptersURI))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.value[0].wordCount").value(44));

		String batchError = Streams.asString(Resources.getResource("batchError.txt").openStream());
		mockMvc.perform(post(batchURI).contentType("multipart/mixed; boundary=batch_1").content(batchError))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("HTTP/1.1 400 Bad Request")))
				.andExpect(content().string(containsString("Page numbers of book 'Not so great Book' chapter 1 are not consistent at number 2")));
	}

	@Test
	public void testSuccessAndErrorBatchesV2() throws Exception {
		String batchSuccess = Streams.asString(Resources.getResource("batchSuccessV2.txt").openStream()).replace("\n", "\r\n");
		mockMvc.perform(post(batchURIV2).contentType("multipart/mixed; boundary=batch_1").content(batchSuccess))
				.andExpect(status().isAccepted());

		mockMvc.perform(get(chaptersURIV2).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.d.results[0].wordCount").value(44));

		String batchError = Streams.asString(Resources.getResource("batchErrorV2.txt").openStream()).replace("\n", "\r\n");
		mockMvc.perform(post(batchURIV2).contentType("multipart/mixed; boundary=batch_1").content(batchError))
				.andExpect(status().isAccepted())
				.andExpect(content().string(containsString("HTTP/1.1 400 Bad Request")))
				.andExpect(content().string(containsString("Page numbers of book 'Not so great Book' chapter 1 are not consistent at number 2")));
	}

	@Test
	public void testCombinedBatch() throws Exception {
		String batch = Streams.asString(Resources.getResource("batchCombined.txt").openStream());
		mockMvc.perform(post(batchURI).contentType("multipart/mixed; boundary=batch_1").content(batch))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("HTTP/1.1 201 Created")))
				.andExpect(content().string(containsString("HTTP/1.1 400 Bad Request")))
				.andExpect(content().string(containsString("Page numbers of book 'Not so great Book' chapter 1 are not consistent at number 2")));
	}

	@Test
	public void testCombinedBatchV2() throws Exception {
		String batch = Streams.asString(Resources.getResource("batchCombinedV2.txt").openStream()).replace("\n", "\r\n");
		mockMvc.perform(post(batchURIV2).contentType("multipart/mixed; boundary=batch_1").content(batch))
				.andExpect(status().isAccepted())
				.andExpect(content().string(containsString("HTTP/1.1 201 Created")))
				.andExpect(content().string(containsString("HTTP/1.1 400 Bad Request")))
				.andExpect(content().string(containsString("Page numbers of book 'Not so great Book' chapter 1 are not consistent at number 2")));
	}
}
