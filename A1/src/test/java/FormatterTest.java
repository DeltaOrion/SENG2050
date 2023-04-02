import com.example.assignment_1.util.WFormatter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FormatterTest {

    @Test
    public void escapeTest() {
        String lt = "<";
        Assertions.assertEquals("&lt;", WFormatter.escapeHTML(lt));
        String gt = ">";
        Assertions.assertEquals("&gt;",WFormatter.escapeHTML(gt));
        String and = "&";
        Assertions.assertEquals("&amp;",WFormatter.escapeHTML(and));

        String html = "<p id=\"hi\">Gam&r</p>";
        Assertions.assertEquals("&lt;p id=&quot;hi&quot;&gt;Gam&amp;r&lt;/p&gt;",WFormatter.escapeHTML(html));
        Assertions.assertEquals("&amp;b&amp;&amp;&amp;&lt;&gt;&gt;j&gt;&#39;h&#39;&#39;&quot;",WFormatter.escapeHTML("&b&&&<>>j>'h''\""));

    }
}
