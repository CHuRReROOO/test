package estagio;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window.Type;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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

	private JFrame Window;
	private JTable table;
	private DefaultTableModel model = new DefaultTableModel();
	private JButton btnNewButton = new JButton("Adicionar novo idlabel");
	private JButton btnNewButton_2 = new JButton("Adicionar Linguaguem");
	private JButton btnNewButton_3 = new JButton("Verificar Duplicados");
	private JButton btnNewButton_4 = new JButton("Remover Duplicados");
	private JButton btnNewButton_5 = new JButton("Traduzir");
	
	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private int screenHeight = screenSize.height;
	private int screenWidth = screenSize.width;

	private boolean tecla = false;
	
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
		// implementar dark mode em linux (FALTA TESTAR)
	
		/* 
		 	API Google Translate:
		 		Plan Basic: $0.00 / mo 
				Characters: 500 / month
				Rate Limit:	5 requests per second
		*/
	// TODO //
	
	@SuppressWarnings("deprecation")
	private void initialize() {
		VerificarOS();
		CriarGrafico();
		AtribuirColunas();
		table.requestFocus();
		
		// atribuir content as linhas //
		try (FileReader path = new FileReader(metodos.Obter_Path(), metodos.Obter_Charset())) {
			JSONObject Obter_Lista_Linguaguens = (JSONObject) new JSONParser().parse(path);
			JSONArray langs = (JSONArray) Obter_Lista_Linguaguens.get("availablelangs");
			JSONArray Componentes_Linguagem = (JSONArray) Obter_Lista_Linguaguens.get("elements");

			for (Iterator<?> iterator = Componentes_Linguagem.iterator(); iterator.hasNext();) {
				JSONObject tudo = (JSONObject) iterator.next();
				JSONObject content_rows = (JSONObject) tudo.get("lang");
			
				Vector<String> arr = new Vector<String>();
				arr.add((String) tudo.get("idlabel"));
				for (int i = 0; i < langs.size(); i++) {
					arr.add((String) content_rows.get(langs.get(i)));
				}
				model.addRow(arr);
				table.requestFocus();
			}
			
			// adicionar nova linha //
			btnNewButton.addMouseListener(new MouseAdapter() {
				@SuppressWarnings("unchecked")
				public void mouseClicked(MouseEvent e) {
					btnNewButton_4.hide();
					String linguaguem = JOptionPane.showInputDialog(Window,"Insira o idlabel que deseja adicionar", null);
					if ((linguaguem == null) || linguaguem.isEmpty()) return;
					for (int i = 0; i < Componentes_Linguagem.size(); i++) {
						JSONObject texto_objeto = (JSONObject) Componentes_Linguagem.get(i);
						if (linguaguem.equals(texto_objeto.get("idlabel"))) {
							JOptionPane.showMessageDialog(new JFrame(), "Este idlabel [" + linguaguem + "] já existe!","Erro: idlabel já existe", JOptionPane.ERROR_MESSAGE);
							return;
						}
					}
					
					JSONObject objeto_id = new JSONObject();
					objeto_id.put("idlabel", linguaguem);
					JSONObject objeto_langs = new JSONObject();
					for (Iterator<?> iterator = langs.iterator(); iterator.hasNext();) {
						String tudo = (String) iterator.next();
						objeto_langs.put(tudo, "");
					}
					objeto_id.put("lang", objeto_langs);
					Componentes_Linguagem.add(objeto_id);
					Obter_Lista_Linguaguens.put("elements", Componentes_Linguagem);
					metodos.Escever_JSON(Obter_Lista_Linguaguens.toString());
					metodos.Setar_Verificacao(true);
					model.addRow(new Object[]{linguaguem});
					Rectangle rect = table.getCellRect(Componentes_Linguagem.size()-1, Componentes_Linguagem.size()-1, true);
					table.scrollRectToVisible(rect);
					table.setRowSelectionInterval(Componentes_Linguagem.size()-1, Componentes_Linguagem.size()-1);
					table.requestFocus();
				}
			});

			// adicionar nova coluna //
			btnNewButton_2.addMouseListener(new MouseAdapter() {
				@SuppressWarnings("unchecked")
				public void mouseClicked(MouseEvent e) {
					btnNewButton_4.hide();
					String linguaguem = JOptionPane.showInputDialog(Window,"Insira a nova linguaguem que deseja adicionar", null);
					if ((linguaguem == null) || linguaguem.isEmpty()) return;
					for (int i = 0; i < metodos.Obter_Arrays_Json().size(); i++) {
						if (linguaguem.equals(metodos.Obter_Arrays_Json().get(i))) {
							JOptionPane.showMessageDialog(new JFrame(), "A linguaguem [" + linguaguem + "] j� existe!","Erro: Linguaguem j� existe", JOptionPane.ERROR_MESSAGE);
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
					metodos.Escever_JSON(Obter_Lista_Linguaguens.toString());
					metodos.Setar_Verificacao(true);
					model.addColumn(linguaguem);
					table.requestFocus();
				}
			});
			
			// verificar duplicados //
			btnNewButton_3.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					btnNewButton_5.setBounds(screenWidth/2 + 400, screenHeight - screenHeight + 35, screenWidth - screenWidth + 180, screenHeight - screenHeight + 30);
					metodos.Setar_Verificacao(true);
					model.setRowCount(0);
					model.setColumnCount(0);
					model.addColumn("idlabel");
					metodos.Setar_Objetos_Json("Array", 1);
					for (int i = 0; i < metodos.Obter_Arrays_Json().size(); i++) {
						model.addColumn(metodos.Obter_Arrays_Json().get(i));
					}
					
					int contagem = 0;
					Set <String> stationCodes = new HashSet<String>();
					Set <String> stationCodes2 = new HashSet<String>();
					
					for (Iterator<?> iterator = Componentes_Linguagem.iterator(); iterator.hasNext();) {
						JSONObject tudo = (JSONObject) iterator.next();
						JSONObject content_rows = (JSONObject) tudo.get("lang");
						String oi = (String) tudo.get("idlabel");
						
						// Verificar idlabels duplicados //
						if(stationCodes.contains(oi)) {
							contagem += 1;
							Vector<String> arr = new Vector<String>();
							arr.add((String) tudo.get("idlabel"));
							for (int i = 0; i < langs.size(); i++) {
								arr.add((String) content_rows.get(langs.get(i)));
							}
							model.addRow(arr);
							continue;
						} else{
							stationCodes.add(oi);
						}
						
						// Verificar default languague duplicada //
						if(stationCodes2.contains(content_rows.get("pt"))) {
							contagem += 1;
							Vector<String> arr2 = new Vector<String>();
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
					table.setDefaultEditor(Object.class, null);
					table.requestFocus();
				}
			});
			
			// REMOVER DUPLICADOS IDLABEL//
			btnNewButton_4.addMouseListener(new MouseAdapter() {
				@SuppressWarnings("unchecked")
				public void mouseClicked(MouseEvent e) {
					Set <String> stationCodes = new HashSet<String>();
					Set <String> stationCodes2 = new HashSet<String>();
					JSONArray tempArray = new JSONArray();
					
					model.setRowCount(0);
					model.setColumnCount(0);

					model.addColumn("idlabel");
					metodos.Setar_Objetos_Json("Array", 1);
					for (int i = 0; i < metodos.Obter_Arrays_Json().size(); i++) {
						model.addColumn(metodos.Obter_Arrays_Json().get(i));
					}
					
					for (Iterator<?> iterator = Componentes_Linguagem.iterator(); iterator.hasNext();) {
						JSONObject tudo = (JSONObject) iterator.next();
						JSONObject content_rows = (JSONObject) tudo.get("lang");
						String oi = (String) tudo.get("idlabel");
						
						// Verificar idlabels duplicados //
						if(stationCodes.contains(oi)) {
							if (!(tempArray.contains(oi))) tempArray.add(tudo);
							continue;
						} else{
							stationCodes.add(oi);
						}
						
						// Verificar default languague duplicada //
//						if(stationCodes2.contains(content_rows.get("pt"))) {
//							if (!(tempArray.contains(tudo))) tempArray.add(tudo);
//							continue;
//						} else {
//							stationCodes2.add((String) content_rows.get("pt"));
//						}
						
						//all.washtaskended
						
						Vector<String> arr = new Vector<String>();
						arr.add((String) tudo.get("idlabel"));
						for (int i = 0; i < langs.size(); i++) {
							arr.add((String) content_rows.get(langs.get(i)));
						}
						model.addRow(arr);
					}
					
					for (int i = 0; i < Componentes_Linguagem.size(); i++) {
						if (i < tempArray.size()) {
							Componentes_Linguagem.remove(tempArray.get(i));
//							Componentes_Linguagem.add(tempArray.get(i));
						}
					}
					
					Obter_Lista_Linguaguens.put("elements", Componentes_Linguagem);
					metodos.Escever_JSON(Obter_Lista_Linguaguens.toString());
					metodos.Setar_Verificacao(true);
					table.requestFocus();
					btnNewButton_4.hide();
					btnNewButton_5.setBounds(screenWidth/2 + 200, screenHeight - screenHeight + 35, screenWidth - screenWidth + 180, screenHeight - screenHeight + 30);
				}
			});
			
			// TRADUZIR DE PT PARA OUTRAS LINGUAGUENS//
			btnNewButton_5.addMouseListener(new MouseAdapter() {
				@SuppressWarnings("unchecked")
				public void mouseClicked(MouseEvent e) {
					JSONArray tempArray = new JSONArray();
					JSONObject palavra = new JSONObject();
					JSONObject palavra_traduzida = new JSONObject();
					
					metodos.Setar_Verificacao(true);
					model.setRowCount(0);
					model.setColumnCount(0);
					
					model.addColumn("idlabel");
					metodos.Setar_Objetos_Json("Array", 1);
					for (int i = 0; i < metodos.Obter_Arrays_Json().size(); i++) {
						model.addColumn(metodos.Obter_Arrays_Json().get(i));
					}
					
					for (Iterator<?> iterator = Componentes_Linguagem.iterator(); iterator.hasNext();) {
						JSONObject tudo = (JSONObject) iterator.next();
						JSONObject content_rows = (JSONObject) tudo.get("lang");
						tempArray.add(content_rows);
					}
					
					palavra = (JSONObject) tempArray.get(metodos.Obter_Numero_Linha());			
								
					for (int i = 1; i < langs.size(); i++) {
						HttpRequest request = HttpRequest.newBuilder() 
								.uri(URI.create("https://google-translate1.p.rapidapi.com/language/translate/v2"))
								.header("content-type", "application/x-www-form-urlencoded")
								.header("Accept-Encoding", "application/gzip")
								.header("X-RapidAPI-Host", "google-translate1.p.rapidapi.com")
								.header("X-RapidAPI-Key", "ae21ce86b8mshe29587aaed14714p18b94fjsn7da5179cc1e8") // ae21ce86b8mshe29587aaed14714p18b94fjsn7da5179cc1e8 // c98c748fd5msh420678769cff72fp104aadjsnb2916da78f6e
								.method("POST", HttpRequest.BodyPublishers.ofString("q=" + palavra.get(langs.get(0))+ "&target=" + langs.get(i)))
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
						} catch (IOException | InterruptedException e1) {
							e1.printStackTrace();
						}
					}
					metodos.Escever_JSON(Obter_Lista_Linguaguens.toString());
					for (Iterator<?> iterator = Componentes_Linguagem.iterator(); iterator.hasNext();) {
						JSONObject tudo = (JSONObject) iterator.next();
						JSONObject content_rows = (JSONObject) tudo.get("lang");
						
						Vector<String> arr = new Vector<String>();
						arr.add((String) tudo.get("idlabel"));
						for (int ii = 0; ii < langs.size(); ii++) {
							arr.add((String) content_rows.get(langs.get(ii)));
						}
						model.addRow(arr);
					}
					Rectangle rect = table.getCellRect(metodos.Obter_Numero_Linha(), metodos.Obter_Numero_Linha(), true);
					table.scrollRectToVisible(rect);
					table.setRowSelectionInterval(metodos.Obter_Numero_Linha(), metodos.Obter_Numero_Linha());
					table.requestFocus();
				}
			});
			
			// PEQUENO FIX A FAZER: availablelangs APARECER EM 2 E NAO EM ULTIMO NO JSON //
			// verificar mudancas nas rows e escrever mudancas no json //
			table.getModel().addTableModelListener(new TableModelListener() {
				@SuppressWarnings("unchecked")
				public void tableChanged(TableModelEvent e) {
					if (metodos.Verificar_Estado_Verificacao())	return;
					if (model.getRowCount() == 0 || model.getColumnCount() == 0) return;
					if (tecla) return;
					if (metodos.Verificar_Estado_Procura()) {
						metodos.Atribuir_Palavra_Nova((JSONObject) Componentes_Linguagem.get(metodos.Obter_Valor_Procura().get(e.getFirstRow()) - 1));
						metodos.Setar_Estado_Procura(true);
					} else {
						metodos.Atribuir_Palavra_Nova((JSONObject) Componentes_Linguagem.get(e.getFirstRow()));
					}
					String palavra_nova_editada = table.getModel().getValueAt(e.getFirstRow(), e.getColumn()) + "";
					for (Iterator<?> iterator = Componentes_Linguagem.iterator(); iterator.hasNext();) {
						JSONObject tudo = (JSONObject) iterator.next();
						JSONObject content_rows = (JSONObject) tudo.get("lang");
						if (tudo.equals(metodos.Obter_Palavra_Nova()) && e.getColumn() != 0) {
							content_rows.replace((langs.get(e.getColumn() - 1)), palavra_nova_editada);
							metodos.Escever_JSON(Obter_Lista_Linguaguens.toString());
							metodos.Setar_Estado_Procura(false);
						}
					}
					table.requestFocus();
				}
			});

			// FUNCAO PARA ACTUALIZAR SE ESTAMOS A ADICIONAR UMA NOVA COLUNA E DAR HANDLE DE ERROS AO ADICIONAR A MESMA //
			ActualizarColuna();

			// Funcao que procura a palavra e mostra na tabela //
			table.addKeyListener(new KeyAdapter() {
				@SuppressWarnings("deprecation")
				public void keyPressed(KeyEvent e) {
					// TECLA ESQ VOLTA Ha TABELA INICIAL MAS COM O CONTEUDO ACTUALIZADO //
					if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						btnNewButton_4.hide();
						
						tecla = true;
						model.setRowCount(0);
						model.setColumnCount(0);

						model.addColumn("idlabel");
						metodos.Setar_Objetos_Json("Array", 1);
						for (int i = 0; i < metodos.Obter_Arrays_Json().size(); i++) {
							model.addColumn(metodos.Obter_Arrays_Json().get(i));
						}

						for (Iterator<?> iterator = Componentes_Linguagem.iterator(); iterator.hasNext();) {
							JSONObject tudo = (JSONObject) iterator.next();
							JSONObject content_rows = (JSONObject) tudo.get("lang");

							Vector<String> arr = new Vector<String>();
							arr.add((String) tudo.get("idlabel"));
							for (int i = 0; i < langs.size(); i++) {
								arr.add((String) content_rows.get(langs.get(i)));
							}
							model.addRow(arr);
						}
						table.requestFocus();
						tecla = false;
					}

					// PROCURAR PALAVRA CTRL + F //
					metodos.Setar_Verificacao(true);
					if ((e.getKeyCode() == KeyEvent.VK_F) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
						btnNewButton_4.hide();
						String procurar_palavra = JOptionPane.showInputDialog(Window,"Insira a palavra que deseja procurar", null);
						if ((procurar_palavra == null) || procurar_palavra.isEmpty()) return;
						int cont = 0;
						model.setRowCount(0);
						for (Iterator<?> iterator = Componentes_Linguagem.iterator(); iterator.hasNext();) {
							Vector<String> arr2 = new Vector<String>();
							Vector<Integer> arr3 = new Vector<Integer>();
							JSONObject tudo = (JSONObject) iterator.next();
							JSONObject content_rows = (JSONObject) tudo.get("lang");
							cont += 1;
							arr2.add((String) tudo.get("idlabel"));
							if (tudo.toString().toLowerCase().contains(procurar_palavra.toLowerCase())) {
								for (int i = 0; i < langs.size(); i++) {
									arr2.add((String) content_rows.get(langs.get(i)));
								}
								arr3.add(cont);
								model.addRow(arr2);
								metodos.Setar_Valor_Procura(arr3);
							}
						}
						table.requestFocus();
						cont = 0;
						metodos.Setar_Verificacao(false);
						metodos.Setar_Estado_Procura(true);
					}
				}
			});
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public void VerificarOS() {
		if (System.getProperty("os.name").contains("Windows")) {
        	RegistryKey windowsPersonalizeKey = new RegistryKey("Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize");
        	RegistryValue systemUsesLightThemeValue = windowsPersonalizeKey.getValue("SystemUsesLightTheme");
        	if (systemUsesLightThemeValue != null) {
        		byte[] data = systemUsesLightThemeValue.getByteData();
        		byte actualValue = data[0];
        		boolean windows10Dark = actualValue == 0;
        		if (windows10Dark) metodos.Setar_Modo_Dark();
        	}
        } else {
        	String s;
        	try {
        		Process p = Runtime.getRuntime().exec("gsettings get org.gnome.desktop.interface gtk-theme");
	        	BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
	        	s = stdInput.readLine();
	        	if (s != null && s.contains("dark")) metodos.Setar_Modo_Dark();
        	} catch (IOException e) {  
        		e.printStackTrace();  
    		    System.out.println("ERROR.RUNNING.CMD"); 
        	}
        }
	}
	
	private void CriarGrafico (){ 
		Window = new JFrame();
		Window.setType(Type.POPUP);
		Window.setTitle("Window");
		Window.setBounds(100, 100, 450, 300);
		Window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Window.setExtendedState(Frame.MAXIMIZED_BOTH);
		Window.setLocationRelativeTo(null);
		Window.getContentPane().setLayout(null);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(5, 100, screenWidth - 15, screenHeight - 180);
		Window.getContentPane().add(scrollPane_1);

		
		table = new JTable(model);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setFillsViewportHeight(true);
		scrollPane_1.setViewportView(table);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		
		btnNewButton.setBounds(screenWidth/2 - 400, screenHeight - screenHeight + 35, screenWidth - screenWidth + 180, screenHeight - screenHeight + 30);
		Window.getContentPane().add(btnNewButton);

		btnNewButton_2.setBounds(screenWidth/2 - 200, screenHeight - screenHeight + 35, screenWidth - screenWidth + 180, screenHeight - screenHeight + 30);
		Window.getContentPane().add(btnNewButton_2);
		
		btnNewButton_3.setBounds(screenWidth/2, screenHeight - screenHeight + 35, screenWidth - screenWidth + 180, screenHeight - screenHeight + 30);
		Window.getContentPane().add(btnNewButton_3);

		btnNewButton_4.setBounds(screenWidth/2 + 200, screenHeight - screenHeight + 35, screenWidth - screenWidth + 180, screenHeight - screenHeight + 30);
		Window.getContentPane().add(btnNewButton_4);
		btnNewButton_4.hide();
		
		btnNewButton_5.setBounds(screenWidth/2 + 200, screenHeight - screenHeight + 35, screenWidth - screenWidth + 180, screenHeight - screenHeight + 30);
		Window.getContentPane().add(btnNewButton_5);

	}

	private void AtribuirColunas () {
		model.addColumn("idlabel");
		metodos.Setar_Objetos_Json("Array", 1);
		for (int i = 0; i < metodos.Obter_Arrays_Json().size(); i++) {
			model.addColumn(metodos.Obter_Arrays_Json().get(i));
		}
	}

	private void ActualizarColuna () {
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				metodos.Setar_Verificacao(false);
				metodos.Guardar_Numero_Linha(table.getSelectedRow());
			}
		});
	}

}
