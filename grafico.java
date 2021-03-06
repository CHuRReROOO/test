package pacote_json;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window.Type;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.registry.RegistryKey;
import com.registry.RegistryValue;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JScrollPane;
import javax.swing.JButton;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

public class grafico {

	private transient JFrame Window;
	private static JScrollPane scrollPane_1;
	private static JTable table;
	private static DefaultTableModel model;
	private static JButton btnNewButton;
	private static JButton btnNewButton_2;
	private static JButton btnNewButton_3;
	private static JButton btnNewButton_4;
	private static JButton btnNewButton_5;
	private static JButton btnNewButton_6;
	private static JButton btnNewButton_7;
	private static JButton btnNewButton_8;
	private static String api = "";	// 38617052b9mshf2a07ad390832f6p101200jsn146916abada4 // c98c748fd5msh420678769cff72fp104aadjsnb2916da78f6e // ae21ce86b8mshe29587aaed14714p18b94fjsn7da5179cc1e8

	private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private final static int screenHeight = screenSize.height;
	private final static int screenWidth = screenSize.width;

	private static JSONObject Obter_Lista_Linguaguens = new JSONObject();
	private static JSONArray langs = new JSONArray();
	private static JSONArray Componentes_Linguagem = new JSONArray();

	private boolean tecla;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					grafico window = new grafico();
					window.Window.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public grafico() {
		initialize();
	}

	// TODO //
	
	/*
	 	API Google Translate: 
	 		Plan Basic: $0.00 / mo 
	 		Characters: 500 / month Rate
	 		Limit: 5 requests per second
	 		Website: https://rapidapi.com/googlecloud/api/google-translate1/
	*/
	
	// TODO //

	private void initialize() {
		VerificarOS();
		CriarGrafico();
		AtribuirColunas();
		table.requestFocus();
//		public void windowIconified(WindowEvent we) {
//			Window.setState(JFrame.NORMAL);
//	        JOptionPane.showMessageDialog(Window, "Cant Minimize");
//	    }
		
		// atribuir content as linhas //
		try(BufferedReader path = Files.newBufferedReader(Paths.get(metodos.ObterPath()), metodos.ObterCharset())) {
			Obter_Lista_Linguaguens = (JSONObject) new JSONParser().parse(path);
			langs = (JSONArray) Obter_Lista_Linguaguens.get("availablelangs");
			Componentes_Linguagem = (JSONArray) Obter_Lista_Linguaguens.get("elements");

			ActualizarNovamentColunas();

			// CRIAR BACKUP AUTOMATICO
			AutoBackup();
			
			// ADICIONAR NOVA LINHA //
			AdicionarLinha();

			// ADICIONAR NOVA COLUNA //
			AdicionarColuna();

			// VERIFICAR DUPLICADOS //
			VerificarDuplicados();

			// REMOVER DUPLICADOS IDLABEL//
			RemoverDuplicados();

			// TRADUZIR DE PT PARA OUTRAS LINGUAGUENS//
			Traduzir();
			
			// PROCURAR COM BOTAO
			ProcurarBotao();
			
			// CRIAR BACKUP
			CriarBackup();
			
			// ABRIR BACKUP
			AbrirBackup();

			// verificar mudancas nas rows e escrever mudancas no json //
			table.getModel().addTableModelListener(new TableModelListener() {
				@SuppressWarnings("unchecked")
				// PEQUENO FIX A FAZER: availablelangs APARECER EM 2 E NAO EM ULTIMO NO JSON //
				public void tableChanged(TableModelEvent e) {
					if (metodos.VerificarEstadoVerificacao()) return;
					if (model.getRowCount() == 0 || model.getColumnCount() == 0) return;
					if (tecla) return;
					if (metodos.VerificarEstadoProcura()) {
						metodos.AtribuirPalavraNova((JSONObject) Componentes_Linguagem.get(metodos.ObterValorProcura().get(e.getFirstRow()) - 1));
						metodos.SetarEstadoProcura(true);
					} else {
						metodos.AtribuirPalavraNova((JSONObject) Componentes_Linguagem.get(e.getFirstRow()));
					}
					String palavra_nova_editada = table.getModel().getValueAt(e.getFirstRow(), e.getColumn()).toString();
					for (Iterator<?> iterator = Componentes_Linguagem.iterator(); iterator.hasNext();) {
						JSONObject tudo = (JSONObject) iterator.next();
						JSONObject content_rows = (JSONObject) tudo.get("lang");
						if (tudo.equals(metodos.ObterPalavraNova()) && e.getColumn() != 0) {
							content_rows.replace((langs.get(e.getColumn() - 1)), palavra_nova_editada);
							metodos.EsceverJSON(Obter_Lista_Linguaguens.toString());
							metodos.SetarEstadoProcura(false);
						}
					}
					table.requestFocus();
				}
			});

			// FUNCAO PARA ACTUALIZAR SE ESTAMOS A ADICIONAR UMA NOVA COLUNA E DAR HANDLE DE
			// ERROS AO ADICIONAR AS MESMAS //
			table.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					metodos.SetarVerificacao(false);
					metodos.GuardarNumeroLinha(table.getSelectedRow());
				}
			});

			// Funcao que procura a palavra e mostra na tabela //
			table.addKeyListener(new KeyAdapter() {
				@SuppressWarnings("deprecation")
				public void keyPressed(KeyEvent e) {
					// TECLA ESQ VOLTA H??? TABELA INICIAL MAS COM O CONTEUDO ACTUALIZADO //
					if (e.getKeyCode() == KeyEvent.VK_ESCAPE ) {
//						DefaultCellEditor singleClickEditor = new DefaultCellEditor(new JTextField());
//						table.setDefaultEditor(Object.class, singleClickEditor);

						btnNewButton_4.hide();

						tecla = true;
						model.setRowCount(0);
						model.setColumnCount(0);

						model.addColumn("idlabel");
						metodos.SetarObjetosJson("Array", 1);
						for (int i = 0; i < metodos.ObterArraysJson().size(); i++) {
							model.addColumn(metodos.ObterArraysJson().get(i));
						}
						
						ActualizarNovamentColunas();
						tecla = false;
						btnNewButton_5.setBounds(screenWidth / 2 - 100, screenHeight - screenHeight + 35, screenWidth - screenWidth + 180, screenHeight - screenHeight + 30);
						btnNewButton_6.setBounds(screenWidth / 2 + 100, screenHeight - screenHeight + 35, screenWidth - screenWidth + 180, screenHeight - screenHeight + 30);
						btnNewButton_7.setBounds(screenWidth / 2 + 300, screenHeight - screenHeight + 35, screenWidth - screenWidth + 180, screenHeight - screenHeight + 30);
						btnNewButton_8.setBounds(screenWidth / 2 + 500, screenHeight - screenHeight + 35, screenWidth - screenWidth + 180, screenHeight - screenHeight + 30);
					}

					// PROCURAR PALAVRA CTRL + F //
					metodos.SetarVerificacao(true);
					if ((e.getKeyCode() == KeyEvent.VK_F) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
						btnNewButton_4.hide();
						String procurar_palavra = JOptionPane.showInputDialog(Window , "Insira a palavra que deseja procurar", "Procurar palavra", JOptionPane.INFORMATION_MESSAGE);
						if ((procurar_palavra == null) || procurar_palavra.isEmpty()) return;
						int cont = 0;
						model.setRowCount(0);
						Vector<String> arr2;
						Vector<Integer> arr3;
						for (Iterator<?> iterator = Componentes_Linguagem.iterator(); iterator.hasNext();) {
							arr2 = new Vector<String>();
							arr3 = new Vector<Integer>();
							final JSONObject tudo = (JSONObject) iterator.next();
							final JSONObject content_rows = (JSONObject) tudo.get("lang");
							cont += 1;
							arr2.add((String) tudo.get("idlabel"));
							if (tudo.toString().toLowerCase().contains(procurar_palavra.toLowerCase())) {
								for (int i = 0; i < langs.size(); i++) {
									arr2.add((String) content_rows.get(langs.get(i)));
								}
								arr3.add(cont);
								model.addRow(arr2);
								metodos.SetarValorProcura(arr3);
							}
						}
						table.requestFocus();
						cont = 0;
						metodos.SetarVerificacao(false);
						metodos.SetarEstadoProcura(true);
						btnNewButton_5.setBounds(screenWidth / 2 - 100, screenHeight - screenHeight + 35, screenWidth - screenWidth + 180, screenHeight - screenHeight + 30);
						btnNewButton_6.setBounds(screenWidth / 2 + 100, screenHeight - screenHeight + 35, screenWidth - screenWidth + 180, screenHeight - screenHeight + 30);
						btnNewButton_7.setBounds(screenWidth / 2 + 300, screenHeight - screenHeight + 35, screenWidth - screenWidth + 180, screenHeight - screenHeight + 30);
						btnNewButton_8.setBounds(screenWidth / 2 + 500, screenHeight - screenHeight + 35, screenWidth - screenWidth + 180, screenHeight - screenHeight + 30);
					}
				}
			});

		} catch (IOException | ParseException e) {
			JOptionPane.showMessageDialog(Window, "Ficheiro JSON n??o encontrado!");
		}
	}

	private void VerificarOS() {
		if (System.getProperty("os.name").contains("Windows")) {
			final RegistryKey windowsPersonalizeKey = new RegistryKey("Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize");
			final RegistryValue systemUsesLightThemeValue = windowsPersonalizeKey.getValue("SystemUsesLightTheme");
			if (systemUsesLightThemeValue != null) {
				final byte[] data = systemUsesLightThemeValue.getByteData();
				final byte actualValue = data[0];
				final boolean windows10Dark = actualValue == 0;
				if (windows10Dark) metodos.SetarModoDark();
			}
		} else {
			final String s;
			try {
				final Process p = Runtime.getRuntime().exec("gsettings get org.gnome.desktop.interface gtk-theme");
				final BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
				s = stdInput.readLine();
				if (s != null && (s.contains("dark")) || s != null && (s.contains("Dark")) ) metodos.SetarModoDark();
				stdInput.close();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(Window, "Erro ao obter o tema default do linux!");
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void CriarGrafico() {
		Window = new JFrame();
		Window.setType(Type.POPUP);
		Window.setTitle("Gr??fico de Tradu????o");
		Window.setBounds(100, 100, 450, 300);
		Window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Window.setExtendedState(Frame.MAXIMIZED_BOTH);
		Window.setLocationRelativeTo(null);
		Window.getContentPane().setLayout(null);
//		Window.setUndecorated(true);
//		Window.setResizable(false);

		scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(5, 100, screenWidth - 15, screenHeight - 168);
		Window.getContentPane().add(scrollPane_1);

		model = new DefaultTableModel();
		table = new JTable(model);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setFillsViewportHeight(true);
		scrollPane_1.setViewportView(table);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);

		btnNewButton = new JButton("Adicionar novo idlabel");
		btnNewButton.setBounds(screenWidth / 2 - 700, screenHeight - screenHeight + 35, screenWidth - screenWidth + 180,screenHeight - screenHeight + 30);
		Window.getContentPane().add(btnNewButton);

		btnNewButton_2 = new JButton("Adicionar Linguaguem");
		btnNewButton_2.setBounds(screenWidth / 2 - 500, screenHeight - screenHeight + 35, screenWidth - screenWidth + 180, screenHeight - screenHeight + 30);
		Window.getContentPane().add(btnNewButton_2);

		btnNewButton_3 = new JButton("Verificar Duplicados");
		btnNewButton_3.setBounds(screenWidth / 2 - 300, screenHeight - screenHeight + 35, screenWidth - screenWidth + 180,screenHeight - screenHeight + 30);
		Window.getContentPane().add(btnNewButton_3);

		btnNewButton_4 = new JButton("Remover Duplicados");
		btnNewButton_4.setBounds(screenWidth / 2 - 100, screenHeight - screenHeight + 35, screenWidth - screenWidth + 180, screenHeight - screenHeight + 30);
		Window.getContentPane().add(btnNewButton_4);
		btnNewButton_4.hide();

		btnNewButton_5 = new JButton("Traduzir");
		btnNewButton_5.setBounds(screenWidth / 2 - 100, screenHeight - screenHeight + 35, screenWidth - screenWidth + 180, screenHeight - screenHeight + 30);
		Window.getContentPane().add(btnNewButton_5);
		
		btnNewButton_6 = new JButton("Procurar");
		btnNewButton_6.setBounds(screenWidth / 2 + 100, screenHeight - screenHeight + 35, screenWidth - screenWidth + 180, screenHeight - screenHeight + 30);
		Window.getContentPane().add(btnNewButton_6);
		
		btnNewButton_7 = new JButton("Fazer Backup");
		btnNewButton_7.setBounds(screenWidth / 2 + 300, screenHeight - screenHeight + 35, screenWidth - screenWidth + 180, screenHeight - screenHeight + 30);
		Window.getContentPane().add(btnNewButton_7);
		
		btnNewButton_8 = new JButton("Abrir Backup");
		btnNewButton_8.setBounds(screenWidth / 2 + 500, screenHeight - screenHeight + 35, screenWidth - screenWidth + 180, screenHeight - screenHeight + 30);
		Window.getContentPane().add(btnNewButton_8);
	}

	private void AtribuirColunas() {
		model.addColumn("idlabel");
		metodos.SetarObjetosJson("Array", 1);
		for (int i = 0; i < metodos.ObterArraysJson().size(); i++) {
			model.addColumn(metodos.ObterArraysJson().get(i));
		}
	}

	private void AdicionarLinha() {
		btnNewButton.addMouseListener(new MouseAdapter() {
			@SuppressWarnings({ "unchecked", "deprecation" })
			public void mouseClicked(MouseEvent e) {
				btnNewButton_4.hide();
				final String id_label = JOptionPane.showInputDialog(Window , "Insira o idlabel que deseja adicionar", "Inserir IDLABEL", JOptionPane.INFORMATION_MESSAGE);
				if ((id_label == null) || id_label.isEmpty()) return;
				JSONObject texto_objeto = new JSONObject ();
				for (int i = 0; i < Componentes_Linguagem.size(); i++) {
					texto_objeto = (JSONObject) Componentes_Linguagem.get(i);
				}
				
				if (id_label.equals(texto_objeto.get("idlabel"))) {
					JOptionPane.showMessageDialog(new JFrame(), "Este idlabel [" + id_label + "] j?? existe!", "Erro: idlabel j?? existe", JOptionPane.ERROR_MESSAGE); 
					return;
				}

				JSONObject objeto_id = new JSONObject();
				objeto_id.put("idlabel", id_label);
				JSONObject objeto_langs = new JSONObject();
				for (Iterator<?> iterator = langs.iterator(); iterator.hasNext();) {
					String tudo = (String) iterator.next();
					objeto_langs.put(tudo, "");
				}
				objeto_id.put("lang", objeto_langs);
				Componentes_Linguagem.add(objeto_id);
				Obter_Lista_Linguaguens.put("elements", Componentes_Linguagem);
				metodos.EsceverJSON(Obter_Lista_Linguaguens.toString());
				metodos.SetarVerificacao(true);
				model.addRow(new Object[] { id_label });
				Rectangle rect = table.getCellRect(Componentes_Linguagem.size() - 1, Componentes_Linguagem.size() - 1,true);
				table.scrollRectToVisible(rect);
				table.setRowSelectionInterval(Componentes_Linguagem.size() - 1, Componentes_Linguagem.size() - 1);
				table.requestFocus();
				JOptionPane.showMessageDialog(Window, "IDLABEL: [" + id_label + "] adicionado com sucesso!");
			}
		});
	}

	private void AdicionarColuna() {
		btnNewButton_2.addMouseListener(new MouseAdapter() {
			@SuppressWarnings({ "unchecked", "deprecation" })
			public void mouseClicked(MouseEvent e) {
				btnNewButton_4.hide();
				final String linguaguem = JOptionPane.showInputDialog(Window , "Insira a nova linguaguem que deseja adicionar", "Inserir Linguaguem", JOptionPane.INFORMATION_MESSAGE);
				if ((linguaguem == null) || linguaguem.isEmpty()) return;
				for (int i = 0; i < metodos.ObterArraysJson().size(); i++) {
					if (linguaguem.equals(metodos.ObterArraysJson().get(i))) {
						JOptionPane.showMessageDialog(new JFrame(), "A linguaguem [" + linguaguem + "] j?? existe!", "Erro: Linguaguem j?? existe", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				langs.add(linguaguem);
				Obter_Lista_Linguaguens.put("availablelangs", langs);
				for (Iterator<?> iterator = Componentes_Linguagem.iterator(); iterator.hasNext();) {
					JSONObject tudo = (JSONObject) iterator.next();
					JSONObject content_rows = (JSONObject) tudo.get("lang");
					content_rows.put(linguaguem, "");
				}
				metodos.EsceverJSON(Obter_Lista_Linguaguens.toString());
				metodos.SetarVerificacao(true);
				model.addColumn(linguaguem);
				table.requestFocus();
				JOptionPane.showMessageDialog(Window, "LINGUAGUEM: [" + linguaguem+ "] adicionada com sucesso!");
			}
		});
	}

	private void VerificarDuplicados() {
		btnNewButton_3.addMouseListener(new MouseAdapter() {
			@SuppressWarnings("deprecation")
			public void mouseClicked(MouseEvent e) {
				tecla = true;
				metodos.SetarVerificacao(true);
				btnNewButton_5.setBounds(screenWidth / 2 + 100, screenHeight - screenHeight + 35,screenWidth - screenWidth + 180, screenHeight - screenHeight + 30);
				btnNewButton_6.setBounds(screenWidth / 2 + 300, screenHeight - screenHeight + 35,screenWidth - screenWidth + 180, screenHeight - screenHeight + 30);
				btnNewButton_7.setBounds(screenWidth / 2 + 500, screenHeight - screenHeight + 35,screenWidth - screenWidth + 180, screenHeight - screenHeight + 30);
				btnNewButton_8.setBounds(screenWidth / 2 + 700, screenHeight - screenHeight + 35,screenWidth - screenWidth + 180, screenHeight - screenHeight + 30);
				model.setRowCount(0);
				model.setColumnCount(0);
				model.addColumn("idlabel");
				metodos.SetarObjetosJson("Array", 1);
				for (int i = 0; i < metodos.ObterArraysJson().size(); i++) {
					model.addColumn(metodos.ObterArraysJson().get(i));
				}

				int contagem = 0;
				final Set<String> stationCodes = new HashSet<String>();
				final Set<String> stationCodes2 = new HashSet<String>();
				Vector <String> arr;
				Vector <String> arr2;

				for (Iterator<?> iterator = Componentes_Linguagem.iterator(); iterator.hasNext();) {
					JSONObject tudo = (JSONObject) iterator.next();
					JSONObject content_rows = (JSONObject) tudo.get("lang");
					String palavara = (String) tudo.get("idlabel");

					// Verificar idlabels duplicados //
					if (stationCodes.contains(palavara)) {
						contagem += 1;
						arr = new Vector<String>();
						arr.add((String) tudo.get("idlabel"));
						for (int i = 0; i < langs.size(); i++) {
							arr.add((String) content_rows.get(langs.get(i)));
						}
						model.addRow(arr);
						continue;
					} else {
						stationCodes.add(palavara);
					}

					// Verificar default languague duplicada //
					if (stationCodes2.contains(content_rows.get("pt"))) {
						contagem += 1;
						arr2 = new Vector<String>();
						arr2.add((String) tudo.get("idlabel"));
						for (int i = 0; i < langs.size(); i++) {
							arr2.add((String) content_rows.get(langs.get(i)));
						}
						model.addRow(arr2);
						continue;
					} else {
						stationCodes2.add((String) content_rows.get("pt"));
					}

				}
				if (contagem > 0) btnNewButton_4.show();
				JOptionPane.showMessageDialog(Window, "Foram encontrados " + contagem + " duplicados!");
				if (contagem <= 0) JOptionPane.showMessageDialog(new JFrame(), "N??o existem palavras duplicadas!", "Erro: n??o existem duplicados", JOptionPane.ERROR_MESSAGE); contagem = 0;
//				table.setDefaultEditor(Object.class, null); // desativar edi????o
				table.requestFocus();
			}
		});
	}

	private void RemoverDuplicados() {
		btnNewButton_4.addMouseListener(new MouseAdapter() {
			@SuppressWarnings({ "unchecked", "deprecation" })
			public void mouseClicked(MouseEvent e) {
				final Set<String> stationCodes = new HashSet<String>();
//				Set <String> stationCodes2 = new HashSet<String>();
				JSONArray tempArray = new JSONArray();
				
				Vector<String> arr;
				
				model.setRowCount(0);
				model.setColumnCount(0);

				model.addColumn("idlabel");
				metodos.SetarObjetosJson("Array", 1);
				for (int i = 0; i < metodos.ObterArraysJson().size(); i++) {
					model.addColumn(metodos.ObterArraysJson().get(i));
				}

				for (Iterator<?> iterator = Componentes_Linguagem.iterator(); iterator.hasNext();) {
					JSONObject tudo = (JSONObject) iterator.next();
					JSONObject content_rows = (JSONObject) tudo.get("lang");
					String palavra = (String) tudo.get("idlabel");

					// Verificar idlabels duplicados //
					if (stationCodes.contains(palavra)) {
						if (!(tempArray.contains(palavra)))	tempArray.add(tudo);
						continue;
					} else {
						stationCodes.add(palavra);
					}

					// Verificar default languague duplicada //
//					if(stationCodes2.contains(content_rows.get("pt"))) {
//						if (!(tempArray.contains(tudo))) tempArray.add(tudo);
//						continue;
//					} else {
//						stationCodes2.add((String) content_rows.get("pt"));
//					}

					// all.washtaskended

					arr = new Vector<String>();
					arr.add((String) tudo.get("idlabel"));
					for (int i = 0; i < langs.size(); i++) {
						arr.add((String) content_rows.get(langs.get(i)));
					}
					model.addRow(arr);
				}

				for (int i = 0; i < Componentes_Linguagem.size(); i++) {
					if (i < tempArray.size()) {
						Componentes_Linguagem.remove(tempArray.get(i));
//						Componentes_Linguagem.add(tempArray.get(i));
					}
				}

				Obter_Lista_Linguaguens.put("elements", Componentes_Linguagem);
				metodos.EsceverJSON(Obter_Lista_Linguaguens.toString());
				metodos.SetarVerificacao(true);
				table.requestFocus();
				JOptionPane.showMessageDialog(Window, "Duplicados removidos com sucesso!");
				btnNewButton_4.hide();
				btnNewButton_5.setBounds(screenWidth / 2 - 100, screenHeight - screenHeight + 35, screenWidth - screenWidth + 180, screenHeight - screenHeight + 30);
				btnNewButton_6.setBounds(screenWidth / 2 + 100, screenHeight - screenHeight + 35, screenWidth - screenWidth + 180, screenHeight - screenHeight + 30);
				btnNewButton_7.setBounds(screenWidth / 2 + 300, screenHeight - screenHeight + 35, screenWidth - screenWidth + 180, screenHeight - screenHeight + 30);
				btnNewButton_8.setBounds(screenWidth / 2 + 500, screenHeight - screenHeight + 35, screenWidth - screenWidth + 180, screenHeight - screenHeight + 30);
			}
		});
	}

	private void Traduzir() {
		btnNewButton_5.addMouseListener(new MouseAdapter() {
			@SuppressWarnings("unchecked")
			public void mouseClicked(MouseEvent e) {
				final JSONArray tempArray = new JSONArray();
				JSONObject palavra = new JSONObject();
				JSONObject palavra_traduzida = new JSONObject();

				metodos.SetarVerificacao(true);
				model.setRowCount(0);
				model.setColumnCount(0);

				model.addColumn("idlabel");
				metodos.SetarObjetosJson("Array", 1);
				for (int i = 0; i < metodos.ObterArraysJson().size(); i++) {
					model.addColumn(metodos.ObterArraysJson().get(i));
				}

				for (Iterator<?> iterator = Componentes_Linguagem.iterator(); iterator.hasNext();) {
					JSONObject tudo = (JSONObject) iterator.next();
					JSONObject content_rows = (JSONObject) tudo.get("lang");
					tempArray.add(content_rows);
				}

				try {
					if (api.isEmpty() || api.length() == 0 || api.equals("")) api = JOptionPane.showInputDialog(Window , "Insira a sua API do google translate", "Inserir API", JOptionPane.INFORMATION_MESSAGE);
					palavra = (JSONObject) tempArray.get(metodos.ObterNumeroLinha());
					for (int i = 1; i < langs.size(); i++) {
						HttpRequest request = HttpRequest.newBuilder()
								.uri(URI.create("https://google-translate1.p.rapidapi.com/language/translate/v2"))
								.header("content-type", "application/x-www-form-urlencoded")
								.header("Accept-Encoding", "application/gzip")
								.header("X-RapidAPI-Host", "google-translate1.p.rapidapi.com")
								.header("X-RapidAPI-Key", api)
								.method("POST",HttpRequest.BodyPublishers.ofString("q=" + palavra.get(langs.get(0)) + "&target=" + langs.get(i)))
								.build();
						HttpResponse<String> response;
						try {
	//						String jsonString = "{\"data\":{\"translations\":[{\"translatedText\":\"Chines\",\"detectedSourceLanguage\":\"pt\"}]}}";
	//						palavra_traduzida = (JSONObject) new JSONParser().parse(jsonString);
							response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
							palavra_traduzida = (JSONObject) JSONValue.parse(response.body());
							JSONObject palavra_traduzida_1 = (JSONObject) palavra_traduzida.get("data");
							JSONArray palavra_traduzida_2 = (JSONArray) palavra_traduzida_1.get("translations");
							JSONObject palavra_traduzida_final = (JSONObject) palavra_traduzida_2.get(0);
							for (Iterator<?> iterator = Componentes_Linguagem.iterator(); iterator.hasNext();) {
								JSONObject tudo = (JSONObject) iterator.next();
								JSONObject content_rows = (JSONObject) tudo.get("lang");
	
								if (palavra.get(langs.get(0)) != null && palavra.get(langs.get(0)) == content_rows.get(langs.get(0))) {
									content_rows.replace(langs.get(i), palavra_traduzida_final.get("translatedText"));
								}
							}
						} catch (IOException | NullPointerException | InterruptedException e1) {
							api = JOptionPane.showInputDialog(Window , "Insira novamente a sua API do google translate", "Erro: Verifique a sua API", JOptionPane.INFORMATION_MESSAGE);
							ActualizarNovamentColunas();
						}
					}
					if (api.length() >= 50) {
						metodos.EsceverJSON(Obter_Lista_Linguaguens.toString());
						ActualizarNovamentColunas();
						Rectangle rect = table.getCellRect(metodos.ObterNumeroLinha(), metodos.ObterNumeroLinha(), true);
						table.scrollRectToVisible(rect);
						table.setRowSelectionInterval(metodos.ObterNumeroLinha(), metodos.ObterNumeroLinha());
						table.requestFocus();
						JOptionPane.showMessageDialog(Window, "Tradu????o realizada com sucesso!");
					}
				} catch (IndexOutOfBoundsException | NullPointerException e1) {
					api = JOptionPane.showInputDialog(Window , "Insira novamente a sua API do google translate", "Erro: Verifique a sua API", JOptionPane.INFORMATION_MESSAGE);
					ActualizarNovamentColunas();
				}
			}
		});
	}
	
	private void ActualizarNovamentColunas () {
		for (Iterator<?> iterator = Componentes_Linguagem.iterator(); iterator.hasNext();) {
			final JSONObject tudo = (JSONObject) iterator.next();
			final JSONObject content_rows = (JSONObject) tudo.get("lang");

			final Vector <String> arr = new Vector<String>();
			arr.add((String) tudo.get("idlabel"));
			for (int i = 0; i < langs.size(); i++) {
				arr.add((String) content_rows.get(langs.get(i)));
			}
			model.addRow(arr);
			table.requestFocus();
		}
	}
	
	private void ProcurarBotao () {
		btnNewButton_6.addMouseListener(new MouseAdapter() {
			@SuppressWarnings("deprecation")
			public void mouseClicked(MouseEvent e) {
				btnNewButton_4.hide();
				String procurar_palavra = JOptionPane.showInputDialog(Window , "Insira a palavra que deseja procurar", "Procurar palavra", JOptionPane.INFORMATION_MESSAGE);
				if ((procurar_palavra == null) || procurar_palavra.isEmpty()) return;
				int cont = 0;
				model.setRowCount(0);
				Vector<String> arr2;
				Vector<Integer> arr3;
				for (Iterator<?> iterator = Componentes_Linguagem.iterator(); iterator.hasNext();) {
					arr2 = new Vector<String>();
					arr3 = new Vector<Integer>();
					final JSONObject tudo = (JSONObject) iterator.next();
					final JSONObject content_rows = (JSONObject) tudo.get("lang");
					cont += 1;
					arr2.add((String) tudo.get("idlabel"));
					if (tudo.toString().toLowerCase().contains(procurar_palavra.toLowerCase())) {
						for (int i = 0; i < langs.size(); i++) {
							arr2.add((String) content_rows.get(langs.get(i)));
						}
						arr3.add(cont);
						model.addRow(arr2);
						metodos.SetarValorProcura(arr3);
					}
				}
				table.requestFocus();
				cont = 0;
				metodos.SetarVerificacao(false);
				metodos.SetarEstadoProcura(true);
			}
		});
	}

	private void CriarBackup () {
		btnNewButton_7.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				String nome_ficheiro = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss'.json'").format(new Date());
				File file = new File(nome_ficheiro);
				try {
					file.createNewFile();
					Files.move(Paths.get(nome_ficheiro), Paths.get("backups/" + nome_ficheiro), StandardCopyOption.REPLACE_EXISTING);
					try(BufferedWriter writeFile = Files.newBufferedWriter(Paths.get("backups/" + nome_ficheiro), metodos.ObterCharset())) {
						writeFile.write(Obter_Lista_Linguaguens.toString());
						writeFile.close();
						metodos.SetarVerificacao(true);
						table.requestFocus();
						JOptionPane.showMessageDialog(Window, "Backup criado com sucesso!");
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(Window, "Pasta de backups n??o encontrada, por favor crie a pasta primeiro!");
					}
				} catch (IOException e2) {
					JOptionPane.showMessageDialog(Window, "Pasta de backups n??o encontrada, por favor crie a pasta primeiro!");
				}
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	private void AutoBackup () {
		String nome_ficheiro = new SimpleDateFormat("'AUTO_BACKUP' dd-MM-yyyy HH-mm-ss'.json'").format(new Date());
		File file = new File(nome_ficheiro);
		try {
			file.createNewFile();
			Files.move(Paths.get(nome_ficheiro), Paths.get("backups/" + nome_ficheiro), StandardCopyOption.REPLACE_EXISTING);
			Obter_Lista_Linguaguens.put("elements", Componentes_Linguagem);
			try(BufferedWriter writeFile = Files.newBufferedWriter(Paths.get("backups/" + nome_ficheiro), metodos.ObterCharset())) {
				writeFile.write(Obter_Lista_Linguaguens.toString());
				writeFile.close();
				metodos.SetarVerificacao(true);
				table.requestFocus();
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(Window, "Pasta de backups n??o encontrada, por favor crie a pasta primeiro!");
			}
		} catch (IOException e2) {
			JOptionPane.showMessageDialog(Window, "Pasta de backups n??o encontrada, por favor crie a pasta primeiro!");
		}
	}
	
	private void AbrirBackup () {
		btnNewButton_8.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				String nome_backup = JOptionPane.showInputDialog(Window , "Insira o nome do backup", "Abrir Backup", JOptionPane.INFORMATION_MESSAGE);
				try(BufferedReader path = Files.newBufferedReader(Paths.get("backups/" + nome_backup), metodos.ObterCharset())) {
					model.setRowCount(0);
					model.setColumnCount(0);
					
					Obter_Lista_Linguaguens = (JSONObject) new JSONParser().parse(path);
					langs = (JSONArray) Obter_Lista_Linguaguens.get("availablelangs");
					Componentes_Linguagem = (JSONArray) Obter_Lista_Linguaguens.get("elements");
					
					model.addColumn("idlabel");
					metodos.SetarObjetosJson("Array", 1);
					for (int i = 0; i < metodos.ObterArraysJson().size(); i++) {
						model.addColumn(metodos.ObterArraysJson().get(i));
					}
					
					for (Iterator<?> iterator = Componentes_Linguagem.iterator(); iterator.hasNext();) {
						final JSONObject tudo = (JSONObject) iterator.next();
						final JSONObject content_rows = (JSONObject) tudo.get("lang");

						final Vector <String> arr = new Vector<String>();
						arr.add((String) tudo.get("idlabel"));
						for (int i = 0; i < langs.size(); i++) {
							arr.add((String) content_rows.get(langs.get(i)));
						}
						model.addRow(arr);
						table.requestFocus();
					}
				JOptionPane.showMessageDialog(Window, "Backup [" + nome_backup + "] carregado com sucesso!");
				} catch (IOException | ParseException e1) {
					JOptionPane.showMessageDialog(Window, "Ficheiro: [" + nome_backup + "] n??o encontrado!");
				}
		    }
		});
	}
}
