package pl.mimuw.zpp.quantumai.utmpython;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

import static java.io.File.createTempFile;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TestController {
    private final SolveService solveService;
    private static final int CONFLICT_RESPONSE_CODE = 409;

    @GetMapping("/hello-world")
    public String test() {
        return "hello world";
    }

    @PostMapping(value = "/solve", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> solve(
            @RequestPart MultipartFile pythonFile,
            @RequestPart MultipartFile inputFile
    ) throws IOException, InterruptedException {
        File python = tempFile(pythonFile);
        File input = tempFile(inputFile);

        String result = solveService.solve(python, input);

        deleteFile(python);
        deleteFile(input);

        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatusCode.valueOf(CONFLICT_RESPONSE_CODE))
                    .body("Turing machine is busy with another task, try again later");
        }
    }

    private static File tempFile(MultipartFile multipartFile) throws IOException {
        File file = createTempFile("temp", null);
        multipartFile.transferTo(file);

        return file;
    }

    private static void deleteFile(File file) {
        if (!file.delete()) {
            log.warn("file {} wasn't deleted successfully", file.getAbsolutePath());
        }
    }
}
