package estagio;
import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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
	private static String Ficheiro_Json = FileSystemView.getFileSystemView().getHomeDirectory() + "/saida.json";
	private static Charset charset = StandardCharsets.UTF_8;
	private static Vector<Integer> Procura_Valor = new Vector<Integer>();
	private static JSONObject palavra_nova = new JSONObject();
	private static JSONObject Objetos = new JSONObject();
	private static JSONArray Arrays = new JSONArray();
	private static boolean Adicionado = false;
	private static boolean Procura = false;
	private static int numero_linha = 0;
	
	public static String Obter_Path () {
		return Ficheiro_Json;
	}
	
	public static Charset Obter_Charset () {
		return charset;
	}
	
	public static boolean Setar_Verificacao(boolean estado) {
		return Adicionado = estado;
	}
	
	public static boolean Verificar_Estado_Verificacao () {
		return Adicionado;
	}
	
	public static boolean Setar_Estado_Procura(boolean estado) {
		return Procura = estado;
	}
	
	public static boolean Verificar_Estado_Procura () {
		return Procura;
	}
	
	public static boolean Setar_Valor_Procura(Vector<Integer> estado) {
		return Procura_Valor.addAll(estado);
	}
	
	public static Vector<Integer> Obter_Valor_Procura () {
		return Procura_Valor;
	}
	
	public static void Atribuir_Palavra_Nova (JSONObject palavra) {
		palavra_nova = palavra;
	}
	
	public static JSONObject Obter_Palavra_Nova () {
		return palavra_nova;
	}
	
	public static void Setar_Objetos_Json (String Objeto_Array, int Numero) {
		try (FileReader path = new FileReader(Obter_Path(), Obter_Charset())) 
		{		
			JSONObject Obter_Lista_Linguaguens = (JSONObject) new JSONParser().parse(path);
			JSONArray langs = (JSONArray) Obter_Lista_Linguaguens.get("availablelangs");
			JSONArray Componentes_Linguagem = (JSONArray) Obter_Lista_Linguaguens.get("elements");
			
			if (Objeto_Array.equals("Objeto") && Numero == 1) Atribuir_Objeto_Json(Obter_Lista_Linguaguens);
			if (Objeto_Array.equals("Array") && Numero == 1) Atribuir_Arrays_Json(langs);
			if (Objeto_Array.equals("Array") && Numero == 2) Atribuir_Arrays_Json(Componentes_Linguagem);
			
			for(Iterator<?> iterator = Componentes_Linguagem.iterator(); iterator.hasNext();) {
				JSONObject tudo = (JSONObject) iterator.next();
				JSONObject content_rows = (JSONObject) tudo.get("lang");
				
				if (Objeto_Array.equals("Objeto") && Numero == 2) Atribuir_Objeto_Json(tudo);
				if (Objeto_Array.equals("Objeto") && Numero == 3) Atribuir_Objeto_Json(content_rows);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	private static void Atribuir_Objeto_Json (JSONObject obj) {
		Objetos = obj;
	}
	
	public static JSONObject Obter_Objeto_Json () {
		return Objetos;
	}

	private static void Atribuir_Arrays_Json (JSONArray Array) {
		Arrays = Array;
	}
	
	public static JSONArray Obter_Arrays_Json () {
		return Arrays;
	}
	
	public static void Escever_JSON (String objeto) {
		FileWriter writeFile = null;
		try {
			writeFile = new FileWriter(Obter_Path(), Obter_Charset());
			writeFile.write(objeto);
			writeFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void Setar_Modo_Dark () {
		UIManager.put("control", new Color(128, 128, 128));
		UIManager.put("info", new Color(128,128,128));
		UIManager.put("nimbusBase", new Color(18, 30, 49));
		UIManager.put("nimbusAlertYellow", new Color(248, 187, 0));
		UIManager.put("nimbusDisabledText", new Color(128, 128, 128));
		UIManager.put("nimbusFocus", new Color(115,164,209));
		UIManager.put("nimbusGreen", new Color(176,179,50));
		UIManager.put("nimbusInfoBlue", new Color(66, 139, 221));
		UIManager.put("nimbusLightBackground", new Color(18, 30, 49));
		UIManager.put("nimbusOrange", new Color(191,98,4));
		UIManager.put("nimbusRed", new Color(169,46,34));
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
	
	public static void Guardar_Numero_Linha (int linha) {
		numero_linha = linha;
	}
	
	
	public static int Obter_Numero_Linha () {
		return numero_linha;
	}
}
