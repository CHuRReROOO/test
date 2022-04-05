package estagio;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileSystemView;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class metodos {
	private static String FicheiroJson = FileSystemView.getFileSystemView().getHomeDirectory() + "/saida.json";
	private static Charset Charset = StandardCharsets.UTF_8;
	private static Vector<Integer> ProcuraValor = new Vector<Integer>();
	private static JSONObject PalavraNova = new JSONObject();
	private static JSONObject Objetos = new JSONObject();
	private static JSONArray Arrays = new JSONArray();
	private static boolean Adicionado;
	private static boolean Procura;
	private static int NumeroLinha;

	public static String ObterPath() {
		return FicheiroJson;
	}

	public static Charset ObterCharset() {
		return Charset;
	}

	public static boolean SetarVerificacao(boolean estado) {
		return Adicionado = estado;
	}

	public static boolean VerificarEstadoVerificacao() {
		return Adicionado;
	}

	public static boolean SetarEstadoProcura(boolean estado) {
		return Procura = estado;
	}

	public static boolean VerificarEstadoProcura() {
		return Procura;
	}

	public static boolean SetarValorProcura(Vector<Integer> arr3) {
		return ProcuraValor.addAll(arr3);
	}

	public static Vector<Integer> ObterValorProcura() {
		return ProcuraValor;
	}

	public static void AtribuirPalavraNova(JSONObject palavra) {
		PalavraNova = palavra;
	}

	public static JSONObject ObterPalavraNova() {
		return PalavraNova;
	}

	public static void SetarObjetosJson(String ObjetoArray, int Numero) {
		try(BufferedReader path = Files.newBufferedReader(Paths.get(ObterPath()), ObterCharset())) {
			final JSONObject Obter_Lista_Linguaguens = (JSONObject) new JSONParser().parse(path);
			final JSONArray langs = (JSONArray) Obter_Lista_Linguaguens.get("availablelangs");
			final JSONArray Componentes_Linguagem = (JSONArray) Obter_Lista_Linguaguens.get("elements");

			if (ObjetoArray.equals("Objeto") && Numero == 1)
				AtribuirObjetoJson(Obter_Lista_Linguaguens);
			if (ObjetoArray.equals("Array") && Numero == 1)
				AtribuirArraysJson(langs);
			if (ObjetoArray.equals("Array") && Numero == 2)
				AtribuirArraysJson(Componentes_Linguagem);
			
			for (final Iterator<?> iterator = Componentes_Linguagem.iterator(); iterator.hasNext();) {
				final JSONObject tudo = (JSONObject) iterator.next();
				final JSONObject content_rows = (JSONObject) tudo.get("lang");

				if (ObjetoArray.equals("Objeto") && Numero == 2)
					AtribuirObjetoJson(tudo);
				if (ObjetoArray.equals("Objeto") && Numero == 3)
					AtribuirObjetoJson(content_rows);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private static void AtribuirObjetoJson(JSONObject obj) {
		Objetos = obj;
	}

	public static JSONObject ObterObjetoJson() {
		return Objetos;
	}

	private static void AtribuirArraysJson(JSONArray Array) {
		Arrays = Array;
	}

	public static JSONArray ObterArraysJson() {
		return Arrays;
	}

	public static void EsceverJSON(String objeto) {
		try(BufferedWriter writeFile = Files.newBufferedWriter(Paths.get(ObterPath()), ObterCharset())) {
			writeFile.write(objeto);
			writeFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void SetarModoDark() {
		UIManager.put("control", new Color(128, 128, 128));
		UIManager.put("info", new Color(128, 128, 128));
		UIManager.put("nimbusBase", new Color(18, 30, 49));
		UIManager.put("nimbusAlertYellow", new Color(248, 187, 0));
		UIManager.put("nimbusDisabledText", new Color(128, 128, 128));
		UIManager.put("nimbusFocus", new Color(115, 164, 209));
		UIManager.put("nimbusGreen", new Color(176, 179, 50));
		UIManager.put("nimbusInfoBlue", new Color(66, 139, 221));
		UIManager.put("nimbusLightBackground", new Color(18, 30, 49));
		UIManager.put("nimbusOrange", new Color(191, 98, 4));
		UIManager.put("nimbusRed", new Color(169, 46, 34));
		UIManager.put("nimbusSelectedText", new Color(255, 255, 255));
		UIManager.put("nimbusSelectionBackground", new Color(104, 93, 156));
		UIManager.put("text", new Color(230, 230, 230));
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void GuardarNumeroLinha(int linha) {
		NumeroLinha = linha;
	}

	public static int ObterNumeroLinha() {
		return NumeroLinha;
	}

}
