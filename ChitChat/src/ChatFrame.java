import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

@SuppressWarnings("serial")
public class ChatFrame extends JFrame implements ActionListener, KeyListener {
	
	private JTextField vzdevek;
	private JTextPane output;
	private JTextPane sodelujoci;
	private JTextField input;
	private JButton prijava;
	private JButton odjava;
	private JTextField zasebno;
	private JComboBox<String> cb;
	
	private boolean isFirstRun=true;
	ReceiveRobot preveriNovaSporocila;

	public ChatFrame() {
		super();
		preveriNovaSporocila = new ReceiveRobot(this);
		setTitle("ChitChat");
		Container pane = this.getContentPane();
		pane.setLayout(new GridBagLayout());
		
		//Prostor za izpis sodelujoèih (prijavljeni), 
		//glavno platno (panel) in
		//polje za vzdevek (napis2).
		FlowLayout layout = new FlowLayout(FlowLayout.LEFT);
		JLabel napis1 = new JLabel(" Sodelujoèi:");
		JPanel panel = new JPanel(layout);
		JLabel napis2 = new JLabel(" Vzdevek:");
		panel.setLayout(layout);
		panel.add(napis2);
		
		GridBagConstraints laConstraint = new GridBagConstraints();
		laConstraint.fill = GridBagConstraints.BOTH;
		laConstraint.gridx = 0;
		laConstraint.gridy = 0;
		pane.add(napis1, laConstraint);
		
		 //Tekstovni prostor za izpis prijavljenih.
		this.sodelujoci = new JTextPane();
		JScrollPane scrolPane = new JScrollPane(sodelujoci);
		scrolPane.setPreferredSize(new Dimension(100, 300)); 
		this.sodelujoci.setEditable(false);
		GridBagConstraints soConstraint = new GridBagConstraints();
		soConstraint.fill = GridBagConstraints.BOTH;
		soConstraint.weightx = 1.0;
		soConstraint.weighty = 1.0;
		soConstraint.gridx = 0;
		soConstraint.gridy = 1;
		pane.add(scrolPane, soConstraint);
		
		 //Možnosti pošiljanja.
		GridBagConstraints lConstraint = new GridBagConstraints();
		lConstraint.fill = GridBagConstraints.BOTH;
		lConstraint.gridx = 0;
		lConstraint.gridy = 2;
		JLabel napis3 = new JLabel(" Možnosti pošiljanja (izberi): ");
		pane.add(napis3, lConstraint);
		
		GridBagConstraints tfConstraint = new GridBagConstraints();
		tfConstraint.fill = GridBagConstraints.BOTH;
		tfConstraint.gridx = 0;
		tfConstraint.gridy = 5;
		
		GridBagConstraints labeConstraint = new GridBagConstraints();
		labeConstraint.fill = GridBagConstraints.BOTH;
		labeConstraint.gridx = 0;
		labeConstraint.gridy = 4;
		JLabel napis5 = new JLabel(" Naslovnik (v primeru ZASEBNO): ");
		pane.add(napis5, labeConstraint);
		
		this.zasebno = new JTextField(40);
		this.zasebno.setEditable(false);
	    pane.add(zasebno, tfConstraint);
	    zasebno.addKeyListener(this);
	    
	    
	    GridBagConstraints cbConstraint = new GridBagConstraints();
		cbConstraint.fill = GridBagConstraints.BOTH;
		cbConstraint.gridx = 0;
		cbConstraint.gridy = 3;
		
		String[] moznosti = { "SKUPINSKO", "ZASEBNO" };
		this.cb = new JComboBox<String>(moznosti);
	    cb.setVisible(true);
	    pane.add(cb, cbConstraint);
	    
		 //Dodam polje za vzdevek na glavno platno.
		this.vzdevek = new JTextField(40);
		String ime = System.getProperty("user.name");
		GridBagConstraints vConstraint = new GridBagConstraints();
		vConstraint.fill = GridBagConstraints.BOTH;
		vConstraint.weightx = 1.0;
		vConstraint.gridx = 1;
		vConstraint.gridy = 0;
		panel.add(vzdevek);
		vzdevek.addKeyListener(this);
		this.vzdevek.setText(ime);
		pane.add(panel, vConstraint);
		
		//Gumba za prijavo in odjavo.
		prijava = new JButton("Prijava");
		odjava = new JButton("Odjava");
		prijava.addActionListener(this);
		odjava.addActionListener(this);
		odjava.setEnabled(false);
		panel.add(prijava);
		panel.add(odjava);
	
		//Tekstovni prostor za izpis pogovora.
		this.output = new JTextPane();
		JScrollPane scrollPane = new JScrollPane(output);
		scrollPane.setPreferredSize(new Dimension(350, 350)); 
		this.output.setEditable(false);
		GridBagConstraints outputConstraint = new GridBagConstraints();
		outputConstraint.fill = GridBagConstraints.BOTH;
		outputConstraint.weightx = 1.0;
		outputConstraint.weighty = 1.0;
		outputConstraint.gridx = 1;
		outputConstraint.gridy = 1;
		pane.add(scrollPane, outputConstraint);
		
		//Tekstovno polje za vnos sporoèila.
		JLabel napis4 = new JLabel("Sporoèilo:");
		GridBagConstraints labConstraint = new GridBagConstraints();
		labConstraint.fill = GridBagConstraints.BOTH;
		labConstraint.gridx = 1;
		labConstraint.gridy = 2;
		pane.add(napis4, labConstraint);
		
		this.input = new JTextField(40);
		this.input.setEditable(false);
		GridBagConstraints inputConstraint = new GridBagConstraints();
		inputConstraint.fill = GridBagConstraints.BOTH;
		inputConstraint.weightx = 1.0;
		inputConstraint.gridx = 1;
		inputConstraint.gridy = 3;
		pane.add(input, inputConstraint);
		input.addKeyListener(this);
		
		//Ko se okno odpre, je fokus na polju za vnos sporoèila.
		addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				input.requestFocusInWindow();
			}
		});
	}

	/**
	 * @param person - the person sending the message
	 * @param message - the message content
	 * @throws BadLocationException 
	 */
	//Zapiše sporoèila, ki jih uporabnik pošlje
	public void dodajSporocilo(String oseba, String sporocilo) throws BadLocationException {
		SimpleAttributeSet set = new SimpleAttributeSet();
	    StyleConstants.setBold(set, true);
	    Document doc = this.output.getStyledDocument();
	    doc.insertString(doc.getLength(), oseba + ": ", set);
	    set = new SimpleAttributeSet();
	    doc.insertString(doc.getLength(), sporocilo + "\n", set);
	}
	//Zapiše prijavljenega
	public void zapisiSodelujocega(Uporabnik oseba) throws BadLocationException {
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		String datum = df.format(oseba.getLastActive());
		
		SimpleAttributeSet attributes = new SimpleAttributeSet();
		attributes = new SimpleAttributeSet();
	    attributes.addAttribute(StyleConstants.CharacterConstants.Bold, Boolean.TRUE);
	    Document doc = this.sodelujoci.getStyledDocument();
	    doc.insertString(doc.getLength()," # " + oseba.getUsername() + "\n" + " Zadnja aktivnost: ", attributes);
	    SimpleAttributeSet set = new SimpleAttributeSet();
	    doc.insertString(doc.getLength(),datum + "\n", set);
	}
	public void pocistiSodelujoce() {
		this.sodelujoci.setText(null);
	}
	
	@Override
	//Prijava in odjava
	public void actionPerformed(ActionEvent e)  {
		if (e.getSource() == prijava) {
	        try {URI uri = new URIBuilder("http://chitchat.andrej.com/users")
					.addParameter("username", this.vzdevek.getText()).build();
				HttpResponse response = Request.Post(uri).execute().returnResponse();
				InputStream responseText = null;
				
				if (response.getStatusLine().getStatusCode()==200) {
				//Uspešna prijava
					if (isFirstRun) {
						preveriNovaSporocila.activate();
						isFirstRun=false;
					}
					this.prijava.setEnabled(false);
					this.odjava.setEnabled(true);
					this.input.setEditable(true);
					this.vzdevek.setEditable(false);
					this.zasebno.setEditable(true);
					responseText=response.getEntity().getContent();
				}else if(response.getStatusLine().getStatusCode()==403){
				//Neuspešna prijava
					responseText=response.getEntity().getContent();
				}
				this.dodajSporocilo("", "-----------------" + getStringFromInputStream(responseText) + "-----------------");
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (URISyntaxException e1) {
				e1.printStackTrace();
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}

        }if (e.getSource() == odjava){
	        try {
	         URI uri = new URIBuilder("http://chitchat.andrej.com/users")
	                .addParameter("username", this.vzdevek.getText())
	                .build();
	         	String responseBody = Request.Delete(uri).execute().returnContent().asString();
	         	this.prijava.setEnabled(true);
				this.odjava.setEnabled(false);
				this.input.setEditable(false);
				this.vzdevek.setEditable(true);
				this.zasebno.setEditable(false);
				this.dodajSporocilo("", "-----------------" + responseBody + "-----------------");
	        } catch (IOException e1) {
	            e1.printStackTrace();
	        } catch (URISyntaxException e1) {
				e1.printStackTrace();
	        } catch (BadLocationException e1) {
				e1.printStackTrace();
			}
        }
         
	}
	
	private static String getStringFromInputStream(InputStream is) {
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		String line;
		try {br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}
	//Preveri, èe so nova sporoèila
	public void sprejmiSporocila() throws URISyntaxException, BadLocationException {
	       try {URI uri = new URIBuilder("http://chitchat.andrej.com/messages")
	    	          .addParameter("username", this.vzdevek.getText())
	    	          .build();

	    	  String responseBody = Request.Get(uri)
	    	                               .execute()
	    	                               .returnContent()
	    	                               .asString();
	    	  ObjectMapper mapper = new ObjectMapper();
	  	  	  mapper.setDateFormat(new ISO8601DateFormat());
	   		
	  	  	  TypeReference<List<Prejeto>> t = new TypeReference<List<Prejeto>>() { };
	  	  	  List<Prejeto> sporocila = mapper.readValue(responseBody, t);
	  	  		
	  	  	  //Zapiše prejeta sporoèila v output
	  	  	  for (Prejeto posta : sporocila) {
	  	  		SimpleAttributeSet attributes = new SimpleAttributeSet();
				attributes = new SimpleAttributeSet();
			    attributes.addAttribute(StyleConstants.CharacterConstants.Bold, Boolean.TRUE);
			    Document doc = this.output.getStyledDocument();
			    doc.insertString(doc.getLength(),posta.getSender() + ": ", attributes);
			    SimpleAttributeSet set = new SimpleAttributeSet();
			    DateFormat df = new SimpleDateFormat("HH:mm:ss");
				String datum = df.format(posta.getSent_at());
			    doc.insertString(doc.getLength(),posta.getText() + "  (" + datum + ")\n", set);
	  	  	  }
	    	  System.out.println(responseBody);
	       } catch (IOException e) {
	    	   e.printStackTrace();
	       }
	}

	@Override
	//Pošlje sporoèilo
	public void keyTyped(KeyEvent e) {
		if (e.getSource() == this.input) {
			if (e.getKeyChar() == '\n') {
				try {URI uri = new URIBuilder("http://chitchat.andrej.com/messages")
		    	          		.addParameter("username", this.vzdevek.getText())
		    	          		.build();
					if (this.cb.getSelectedItem() == "ZASEBNO") {
						String message = "{ \"global\" : false, \"recipient\" : \"" + this.zasebno.getText()
								+ "\", \"text\" : \"" + this.input.getText() + "\"}";
						String responseBody = Request.Post(uri)
			    	  			.bodyString(message, ContentType.APPLICATION_JSON)
			    	  			.execute()
			    	  			.returnContent()
			    	  			.asString();
						System.out.println(responseBody);
					} else {
						String message = "{ \"global\" : true, \"text\" : \"" + this.input.getText() + "\"  }";
						String responseBody = Request.Post(uri)
								.bodyString(message, ContentType.APPLICATION_JSON)
								.execute()
								.returnContent()
								.asString();
						System.out.println(responseBody);
					}
					this.dodajSporocilo(this.vzdevek.getText(), this.input.getText());
		    	  	this.input.setText(null);
				} catch (URISyntaxException e1) {
					e1.printStackTrace();
				} catch (ClientProtocolException e2) {
					e2.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
