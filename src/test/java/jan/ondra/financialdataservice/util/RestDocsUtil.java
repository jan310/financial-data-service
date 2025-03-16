package jan.ondra.financialdataservice.util;

import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;

import static org.springframework.http.HttpHeaders.CONTENT_LENGTH;
import static org.springframework.http.HttpHeaders.HOST;
import static org.springframework.http.HttpHeaders.VARY;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

public class RestDocsUtil {

    public static RestDocumentationResultHandler documentResult(String identifier) {
        return document(
            identifier,
            preprocessRequest(prettyPrint(), modifyHeaders().remove(HOST).remove(CONTENT_LENGTH)),
            preprocessResponse(prettyPrint(), modifyHeaders().remove(VARY).remove(CONTENT_LENGTH))
        );
    }

}
