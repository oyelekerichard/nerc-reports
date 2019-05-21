package com.crowninteractive.net.nercreport;

import com.crowninteractive.net.nercreport.exception.NercReportException;
import com.crowninteractive.net.nercreport.jms.ReportReceiver;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.commons.mail.EmailException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NercReportApplicationTests {

    @Autowired
    private ReportReceiver rep;

    @Test
    public void test() throws IOException, FileNotFoundException, NercReportException, EmailException {
        try {
            rep.processWriteV4("2018-11-01", "2018-11-30", "oluwaseun.olaoye@crowninteractive.com");
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
    }

}
