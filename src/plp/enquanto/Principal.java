package plp.enquanto;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import plp.enquanto.linguagem.Linguagem.Programa;
import plp.enquanto.parser.EnquantoLexer;
import plp.enquanto.parser.EnquantoParser;
import plp.enquanto.parser.MeuListener;

public class Principal {

	private static ParseTree parse(String programa) {
		final ANTLRInputStream input = new ANTLRInputStream(programa);
		final EnquantoLexer lexer = new EnquantoLexer(input);
		final CommonTokenStream tokens = new CommonTokenStream(lexer);
		final EnquantoParser parser = new EnquantoParser(tokens);
		return parser.programa();
	}

	public static void main(String... args) throws IOException {
		Programa programa;
		if(args.length > 0) {
			try {
				final Path path = Paths.get(args[0]);
				final List<String> linhas = Files.readAllLines(path, StandardCharsets.UTF_8);
				final StringBuilder codigo = new StringBuilder();
				for(final String linha : linhas) 
					codigo.append(linha).append("\n");		
				final ParseTree tree = parse(codigo.toString());
				final ParseTreeWalker walker = new ParseTreeWalker();
				final MeuListener listener = new MeuListener();
				walker.walk(listener, tree);
				programa = listener.getPrograma();
				programa.execute();
			} catch (Exception e) {
				System.err.println("Não foi possivel executar o codigo");
			}
			
		} else {
			final ParseTree tree = parse("exiba \"Bem vindo a linguagem Enquanto\"");
			final ParseTreeWalker walker = new ParseTreeWalker();
			final MeuListener listener = new MeuListener();
			walker.walk(listener, tree);
			programa = listener.getPrograma();
			programa.execute();
		}
		
	}
}
