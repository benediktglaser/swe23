package at.qe.g1t2.tests;

import at.qe.g1t2.restapi.model.ResponsePicture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;



@SpringBootTest
public class ResponsePictureTest {
    @Test
    public void testResponsePicture() {
        ResponsePicture picture = new ResponsePicture("mint.jpg", "/user/download", ".jpg", 800);
        picture.setFileName("carrot.png");
        picture.setFileDownloadUri("user/downloads/pictures");
        picture.setFileType(".png");
        picture.setSize(400);

        Assertions.assertEquals("carrot.png", picture.getFileName());
        Assertions.assertEquals("user/downloads/pictures", picture.getFileDownloadUri());
        Assertions.assertEquals(".png", picture.getFileType());
        Assertions.assertEquals(400, picture.getSize());


    }
}
