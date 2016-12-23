package plp.enquanto.parser;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import plp.enquanto.linguagem.Linguagem.Atribuicao;
import plp.enquanto.linguagem.Linguagem.Bloco;
import plp.enquanto.linguagem.Linguagem.Bool;
import plp.enquanto.linguagem.Linguagem.Booleano;
import plp.enquanto.linguagem.Linguagem.Comando;
import plp.enquanto.linguagem.Linguagem.ELogico;
import plp.enquanto.linguagem.Linguagem.Enquanto;
import plp.enquanto.linguagem.Linguagem.Escreva;
import plp.enquanto.linguagem.Linguagem.Exiba;
import plp.enquanto.linguagem.Linguagem.ExpBin;
import plp.enquanto.linguagem.Linguagem.ExpDiferente;
import plp.enquanto.linguagem.Linguagem.ExpDiv;
import plp.enquanto.linguagem.Linguagem.ExpIgual;
import plp.enquanto.linguagem.Linguagem.ExpMaior;
import plp.enquanto.linguagem.Linguagem.ExpMaiorIgual;
import plp.enquanto.linguagem.Linguagem.ExpMenor;
import plp.enquanto.linguagem.Linguagem.ExpMenorIgual;
import plp.enquanto.linguagem.Linguagem.ExpMult;
import plp.enquanto.linguagem.Linguagem.ExpPot;
import plp.enquanto.linguagem.Linguagem.ExpRel;
import plp.enquanto.linguagem.Linguagem.ExpSoma;
import plp.enquanto.linguagem.Linguagem.ExpSub;
import plp.enquanto.linguagem.Linguagem.Expressao;
import plp.enquanto.linguagem.Linguagem.Id;
import plp.enquanto.linguagem.Linguagem.Inteiro;
import plp.enquanto.linguagem.Linguagem.Leia;
import plp.enquanto.linguagem.Linguagem.NaoLogico;
import plp.enquanto.linguagem.Linguagem.OULogico;
import plp.enquanto.linguagem.Linguagem.Para;
import plp.enquanto.linguagem.Linguagem.Programa;
import plp.enquanto.linguagem.Linguagem.Se;
import plp.enquanto.linguagem.Linguagem.Skip;
import plp.enquanto.linguagem.Linguagem.XORLogico;
import plp.enquanto.parser.EnquantoParser.ComandoContext;
import plp.enquanto.parser.EnquantoParser.OuLogicoContext;
import plp.enquanto.parser.EnquantoParser.ParaContext;
import plp.enquanto.parser.EnquantoParser.XorLogicoContext;

public class MeuListener extends EnquantoBaseListener {
	private final Leia leia = new Leia();
	private final Skip skip = new Skip();
	private final ParseTreeProperty<Object> values = new ParseTreeProperty<>();

	private Programa programa;

	public Programa getPrograma() {
		return programa;
	}

	private void setValue(final ParseTree node, final Object value) {
		values.put(node, value);
	}

	private Object getValue(final ParseTree node) {
		return values.get(node);
	}

	@Override
	public void exitBooleano(final EnquantoParser.BooleanoContext ctx) {
		setValue(ctx, new Booleano(ctx.getText().equals("verdadeiro")));
	}

	@Override
	public void exitLeia(final EnquantoParser.LeiaContext ctx) {
		setValue(ctx, leia);
	}

	@Override
	public void exitSe(final EnquantoParser.SeContext ctx) {
		final List<Bool> bools = new ArrayList<>();
		final List<Comando> comands = new ArrayList<>();
		for (EnquantoParser.BoolContext c : ctx.bool()) {
			bools.add((Bool) getValue(c));
		}
		for(ComandoContext c : ctx.comando()){
			comands.add((Comando) getValue(c));
		}
		setValue(ctx, new Se(bools, comands));
	}

	@Override
	public void exitInteiro(final EnquantoParser.InteiroContext ctx) {
		setValue(ctx, new Inteiro(Integer.parseInt(ctx.getText())));
	}

	@Override
	public void exitSkip(final EnquantoParser.SkipContext ctx) {
		setValue(ctx, skip);
	}

	@Override
	public void exitEscreva(final EnquantoParser.EscrevaContext ctx) {
		final Expressao exp = (Expressao) getValue(ctx.expressao());
		setValue(ctx, new Escreva(exp));
	}

	@Override
	public void exitPrograma(final EnquantoParser.ProgramaContext ctx) {
		@SuppressWarnings("unchecked")
		final List<Comando> cmds = (List<Comando>) getValue(ctx.seqComando());
		programa = new Programa(cmds);
		setValue(ctx, programa);
	}

	@Override
	public void exitId(final EnquantoParser.IdContext ctx) {
		setValue(ctx, new Id(ctx.ID().getText()));
	}

	@Override
	public void exitSeqComando(final EnquantoParser.SeqComandoContext ctx) {
		final List<Comando> comandos = new ArrayList<>();
		for (EnquantoParser.ComandoContext c : ctx.comando()) {
			comandos.add((Comando) getValue(c));
		}
		setValue(ctx, comandos);
	}

	@Override
	public void exitAtribuicao(final EnquantoParser.AtribuicaoContext ctx) {
		final String id = ctx.ID().getText();
		final Expressao exp = (Expressao) getValue(ctx.expressao());
		setValue(ctx, new Atribuicao(id, exp));
	}

	@Override
	public void exitBloco(final EnquantoParser.BlocoContext ctx) {
		@SuppressWarnings("unchecked")
		final List<Comando> cmds = (List<Comando>) getValue(ctx.seqComando());
		setValue(ctx, new Bloco(cmds));
	}

	@Override
	public void exitOpBin(final EnquantoParser.OpBinContext ctx) {
		final Expressao esq = (Expressao) getValue(ctx.expressao(0));
		final Expressao dir = (Expressao) getValue(ctx.expressao(1));
		final String op = ctx.getChild(1).getText();
		final ExpBin exp;
		switch (op) {
		case "+":
			exp = new ExpSoma(esq, dir);
			break;
		case "*":
			exp = new ExpMult(esq, dir);
			break;
		case "-":
			exp = new ExpSub(esq, dir);
			break;
		case "/":
			exp = new ExpDiv(esq, dir);
			break;
		case "^":
			exp = new ExpPot(esq, dir);
			break;
		default:
			exp = new ExpSoma(esq, dir);
		}
		setValue(ctx, exp);
	}

	@Override
	public void exitEnquanto(final EnquantoParser.EnquantoContext ctx) {
		final Bool condicao = (Bool) getValue(ctx.bool());
		final Comando comando = (Comando) getValue(ctx.comando());
		setValue(ctx, new Enquanto(condicao, comando));
	}

	@Override
	public void exitELogico(final EnquantoParser.ELogicoContext ctx) {
		final Bool esq = (Bool) getValue(ctx.bool(0));
		final Bool dir = (Bool) getValue(ctx.bool(1));
		setValue(ctx, new ELogico(esq, dir));
	}

	@Override
	public void exitBoolPar(final EnquantoParser.BoolParContext ctx) {
		setValue(ctx, getValue(ctx.bool()));
	}

	@Override
	public void exitNaoLogico(final EnquantoParser.NaoLogicoContext ctx) {
		final Bool b = (Bool) getValue(ctx.bool());
		setValue(ctx, new NaoLogico(b));
	}

	@Override
	public void exitExpPar(final EnquantoParser.ExpParContext ctx) {
		setValue(ctx, getValue(ctx.expressao()));
	}

	@Override
	public void exitExiba(final EnquantoParser.ExibaContext ctx) {
		final String t = ctx.Texto().getText();
		final String texto = t.substring(1, t.length() - 1);
		setValue(ctx, new Exiba(texto));
	}

	@Override
	public void exitOpRel(final EnquantoParser.OpRelContext ctx) {
		final Expressao esq = (Expressao) getValue(ctx.expressao(0));
		final Expressao dir = (Expressao) getValue(ctx.expressao(1));
		final String op = ctx.getChild(1).getText();
		final ExpRel exp;
		switch (op) {
		case "=":
			exp = new ExpIgual(esq, dir);
			break;
		case ">":
			exp = new ExpMaior(esq, dir);
			break;
		case ">=":
			exp = new ExpMaiorIgual(esq, dir);
			break;
		case "<":
			exp = new ExpMenor(esq, dir);
			break;
		case "<=":
			exp = new ExpMenorIgual(esq, dir);
			break;
		case "<>":
			exp = new ExpDiferente(esq, dir);
			break;
		default:
			exp = new ExpIgual(esq, dir);
		}
		setValue(ctx, exp);
	}
	
	@Override
	public void exitOuLogico(OuLogicoContext ctx) {
		final Bool esq = (Bool) getValue(ctx.bool(0));
		final Bool dir = (Bool) getValue(ctx.bool(1));
		setValue(ctx, new OULogico(esq, dir));
	}
	
	@Override
	public void exitXorLogico(XorLogicoContext ctx) {
		final Bool esq = (Bool) getValue(ctx.bool(0));
		final Bool dir = (Bool) getValue(ctx.bool(1));
		setValue(ctx, new XORLogico(esq, dir));
	}
	
	@Override
	public void exitPara(ParaContext ctx) {
		final String id = ctx.ID().getText();
		final Expressao expDe = (Expressao) getValue(ctx.expressao(0));
		final Expressao expAte = (Expressao) getValue(ctx.expressao(1));
		final Comando comando = (Comando) getValue(ctx.comando());
		setValue(ctx, new Para(id, expDe, expAte, comando));
	}
	
}
