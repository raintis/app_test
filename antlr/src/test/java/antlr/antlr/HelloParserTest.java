package antlr.antlr;

import java.io.ByteArrayInputStream;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class HelloParserTest {

	public static void main(String[] args) throws Exception {
		ByteArrayInputStream is = new ByteArrayInputStream(("yyy hello wiki").getBytes());
		ANTLRInputStream input = new ANTLRInputStream(is);
		HelloLexer lexer = new HelloLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		HelloParser parser = new HelloParser(tokens);

        ParseTree tree = parser.r();
        System.out.println(tree.toStringTree(parser));
	}
}
