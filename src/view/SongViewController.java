//NAMES: Param Thakker(pkt15), Jonathan Lu (jnl76)
package view;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.event.ActionEvent;



public class SongViewController {
	@FXML         
	ListView<String> listView;             
	@FXML
	Button add;
	@FXML
	Button del;
	@FXML
	Button edit;
	@FXML
	Button save;
	@FXML
	Button saveAdd;
	@FXML
	Button cancelAdd;
	@FXML 
	Button cancelEdit;
	@FXML
	TextField nameDet;
	@FXML
	TextField artistDet;
	@FXML
	TextField albumDet;
	@FXML
	TextField yearDet;
	@FXML
	TextField rTitle;
	//for edit duplicate check purposes
	String ogName = "";
	String ogArtist = "";
	
	private ObservableList<String> obsList;  
	Map<String,List<String>> map=new HashMap<>();
	List<String> list=new ArrayList<>();
	public void start(Stage mainStage) throws IOException {                 
		
		
		File file = new File("songs.txt"); 
		Scanner sc = new Scanner(file); 
		    if (!sc.hasNextLine()) {
		    	System.out.println("Empty file");
		    	return;
		    }
		    while (sc.hasNextLine()) { 
		    	String item=sc.nextLine().trim();
		    	if (item.equals("")) {
		    		continue;
		    	}
		    	String[] content=item.split("\\|",4);
		        list.add(content[0].trim()+ "|" + content[1].trim());
		        map.put(content[0].trim()+ "|" + content[1].trim(), new ArrayList<>(Arrays.asList(content[2].trim(),content[3].trim())));
		    }
		  
		
		Collections.sort(list,new sortSongs());
		
		obsList = FXCollections.observableArrayList(list); 

		listView.setItems(obsList); 

		// select the first item
		if (!list.isEmpty()) {
			listView.getSelectionModel().select(0);
			songDetailV2(); //displays song deets without user having to interact
		}
		// set listener for the items
		listView
		.getSelectionModel()
		.selectedIndexProperty()
		.addListener(
				(obs, oldVal, newVal) -> 
				songDetail(mainStage));

	}
	
	private void songDetail(Stage mainStage) { 
		if (!listView.getSelectionModel().isEmpty()) {
			String[] song=listView.getSelectionModel().getSelectedItem().split("\\|",3);
			List<String> songDets=map.get(listView.getSelectionModel().getSelectedItem());
			
			String album=songDets.get(0);
			String year=songDets.get(1);
			//display current song info in details pane
			nameDet.setText(song[0]);
			artistDet.setText(song[1]);
			albumDet.setText(album);
			yearDet.setText(year);
			
		}
	}
	private void songDetailV2() { 
		
		String[] song=listView.getSelectionModel().getSelectedItem().split("\\|",3);
		List<String> songDets=map.get(listView.getSelectionModel().getSelectedItem());
		
		String album=songDets.get(0);
		String year=songDets.get(1);
		//display current song info in details pane
		nameDet.setText(song[0]);
		artistDet.setText(song[1]);
		albumDet.setText(album);
		yearDet.setText(year);
	
	
}
	public void addSong(ActionEvent e) {
		rTitle.setText("Add Song:");
		//show relevant buttons
		nameDet.setEditable(true);
		artistDet.setEditable(true);
		albumDet.setEditable(true);
		yearDet.setEditable(true);
		nameDet.clear();
		artistDet.clear();
		albumDet.clear();
		yearDet.clear();
		saveAdd.setVisible(true);
		listView.setMouseTransparent(true);
		listView.setFocusTraversable(false);
		del.setDisable(true);
		edit.setDisable(true);
		cancelAdd.setVisible(true);
	}
	public void saveAdd(ActionEvent e) throws IOException {
		Alert alert = new Alert(AlertType.CONFIRMATION, "Save changes?\nName:"
															+ nameDet.getText() +
															"\nArtist: " + artistDet.getText() +
															"\nAlbum: " + albumDet.getText() +
															"\nYear: " + yearDet.getText(), 
															ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
		alert.showAndWait();
		//show the relevant buttons
		int index = listView.getSelectionModel().getSelectedIndex();
		nameDet.setEditable(false);
		artistDet.setEditable(false);
		albumDet.setEditable(false);
		yearDet.setEditable(false);
		listView.setMouseTransparent(false);
		listView.setFocusTraversable(true);
		del.setDisable(false);
		edit.setDisable(false);
		
		
		//add new song using same implementation as beginning
		if (alert.getResult() == ButtonType.YES) {
			
			String errMsg = "";
			//check empty
			if (nameDet.getText().equals("") || (artistDet.getText().equals(""))) {
				errMsg+= "NAME OR ARTIST CANNOT BE EMPTY!"; 
				Alert badAdd = new Alert(AlertType.ERROR);
				badAdd.setHeaderText(errMsg);
				badAdd.showAndWait();
			}//check duplicates
			else if (map.containsKey(nameDet.getText().trim() + "|" + artistDet.getText().trim())) {
				errMsg+= nameDet.getText() + " by " + artistDet.getText() + " is already in the library! ";
				Alert dupAdd = new Alert(AlertType.ERROR);
				dupAdd.setHeaderText(errMsg);
				dupAdd.showAndWait();
				clearText();
			}//year isn't a positive integer
			else if (!yearDet.getText().equals("") &&  (!isInteger(yearDet.getText()) || Integer.valueOf(yearDet.getText())<=0)) {
				errMsg += yearDet.getText() + " is not a valid YEAR!";
				Alert badYear = new Alert(AlertType.ERROR);
				badYear.setHeaderText(errMsg);
				badYear.showAndWait();
				clearText();
			}//add new song
			else {
				String item = nameDet.getText() + " | " + artistDet.getText() + " | " + albumDet.getText() + " | " + yearDet.getText();
		    	String[] content=item.split("\\|",4);
		        list.add(content[0].trim()+ "|" + content[1].trim());
		        map.put(content[0].trim()+ "|" + content[1].trim(), new ArrayList<>(Arrays.asList(content[2].trim(),content[3].trim())));
		        
		        Collections.sort(list, new sortSongs());
		        obsList = FXCollections.observableArrayList(list); 
				listView.setItems(obsList);
				FileWriter fw = new FileWriter("songs.txt", true);
	            fw.write("\n" + item);
	            fw.close();
				
			}
			cancelAdd.setVisible(false);
			saveAdd.setVisible(false);
		}else {
			cancel();
		}
		rTitle.setText("Song Details");
		if (listView.getSelectionModel().getSelectedItem() == null) {
			listView.getSelectionModel().select(0);
			songDetailV2();
		}else {
			listView.getSelectionModel().select(index);
		}
	}
	public void deleteSong(ActionEvent e) throws IOException {
		if (listView.getSelectionModel().getSelectedItem() != null) {
			String itemToBeRemoved=listView.getSelectionModel().getSelectedItem();
			Alert alert = new Alert(AlertType.CONFIRMATION, "Delete " + listView.getSelectionModel().getSelectedItem() + " ?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
			alert.showAndWait();
			File file = new File("songs.txt"); 
			Scanner sc = new Scanner(file); 
			String contentOfFile="";

			int currIndex = listView.getSelectionModel().getSelectedIndex();
			if (alert.getResult() == ButtonType.YES) {
				listView.getItems().remove(currIndex);
				list.remove(currIndex);
				while (sc.hasNextLine()) {
			    	String original=sc.nextLine();
			    	String item=original.trim();
			    	if (item.equals("")) {
			    		continue;
			    	}
			    	String[] content=item.split("\\|",4);
			    	if ((content[0].trim()+ "|" + content[1].trim()).equals(itemToBeRemoved)){
			    		contentOfFile+="\n";
			    	}
			    	else {
			    		contentOfFile+=original + "\n";
			    	}
				}			
				FileWriter fw = new FileWriter("songs.txt");
				fw.write(contentOfFile);
				fw.close();	
			}
			
			if (!listView.getSelectionModel().isEmpty()) { //if not empty make a selection
				if (listView.getSelectionModel().getSelectedItem() != null) {
					listView.getSelectionModel().select(currIndex);
				}else {
					listView.getSelectionModel().select(currIndex-1);
				}
				songDetailV2(); //displays details of the next song without the user having to interact
			}else {
				clearText();
			}
			
		}else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setHeaderText("No selected item to delete!");
			alert.showAndWait();

		}
	}
	public void editSong(ActionEvent e) throws IOException {
		//show relevant buttons
		if (listView.getSelectionModel().getSelectedItem() != null) {
			ogName = nameDet.getText();
			ogArtist = artistDet.getText();
			
			nameDet.setEditable(true);
			artistDet.setEditable(true);
			albumDet.setEditable(true);
			yearDet.setEditable(true);
			save.setVisible(true);
			listView.setMouseTransparent(true);
			listView.setFocusTraversable(false);
			del.setDisable(true);
			add.setDisable(true);
			cancelEdit.setVisible(true);
		}else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setHeaderText("No selected item to edit!");
			alert.showAndWait();

		}
	}
	public void saveSong(ActionEvent e) throws IOException {
		String[] song=listView.getSelectionModel().getSelectedItem().split("\\|",2);
		Alert alert = new Alert(AlertType.CONFIRMATION, "Save changes?" + "\nName: "
															+ nameDet.getText() +
															"\nArtist: " + artistDet.getText() +
															"\nAlbum: " + albumDet.getText() +
															"\nYear: " + yearDet.getText(), 
															ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
		alert.showAndWait();
		//show relevant buttons
		nameDet.setEditable(false);
		artistDet.setEditable(false);
		albumDet.setEditable(false);
		yearDet.setEditable(false);
		listView.setMouseTransparent(false);
		listView.setFocusTraversable(true);
		del.setDisable(false);
		add.setDisable(false);
		boolean songNameArtEdited = !ogName.equals(nameDet.getText()) || !ogArtist.equals(artistDet.getText());
		if (alert.getResult() == ButtonType.YES) {
			
			String errMsg = "";
			//check empty
			if (nameDet.getText().equals("") || (artistDet.getText().equals(""))) { 
				errMsg+= "NAME OR ARTIST CANNOT BE EMPTY!";
				Alert badAdd = new Alert(AlertType.ERROR);
				badAdd.setHeaderText(errMsg);
				badAdd.showAndWait();
				clearText();
				
			}//year isn't a positive integer
			else if (!yearDet.getText().equals("") &&  (!isInteger(yearDet.getText()) || Integer.valueOf(yearDet.getText())<=0)) {
				errMsg += yearDet.getText() + " is not a valid YEAR!";
				Alert badYear = new Alert(AlertType.ERROR);
				badYear.setHeaderText(errMsg);
				badYear.showAndWait();
				clearText();
			}//check duplicates
			else if (songNameArtEdited && map.containsKey(nameDet.getText().trim() + "|" + artistDet.getText().trim())) {
				errMsg+= nameDet.getText() + " by " + artistDet.getText() + " is already in the library! ";
				Alert dupAdd = new Alert(AlertType.ERROR);
				dupAdd.setHeaderText(errMsg);
				dupAdd.showAndWait();
				clearText();
			}//edit success
			else {
				list.clear();
				map.remove(song[0]+ "|" + song[1]);
				map.put(nameDet.getText()+"|" + artistDet.getText(), new ArrayList<>(Arrays.asList(albumDet.getText(),yearDet.getText())));
				int index = listView.getSelectionModel().getSelectedIndex();
				obsList.set(index, nameDet.getText()+"|" + artistDet.getText());
				File file = new File("songs.txt"); 
				Scanner sc = new Scanner(file); 
				String contentOfFile="";
					while (sc.hasNextLine()) {
				    	String original=sc.nextLine();
				    	String item=original.trim();
				    	if (item.equals("")) {
				    		continue;
				    	}
				    	String[] content=item.split("\\|",4);
				    	if ((content[0].trim()+ "|" + content[1].trim()).equals(song[0]+ "|" + song[1])){
				    		contentOfFile+=nameDet.getText()+ " | " + artistDet.getText() + " | " + albumDet.getText() + " | " + yearDet.getText() +"\n";
				    		list.add(nameDet.getText()+ "|" + artistDet.getText());
				    	}
				    	else {
				    		contentOfFile+=original + "\n";
				    		list.add(content[0].trim()+ "|" + content[1].trim());
				    	}
					}		
					
					FileWriter fw = new FileWriter("songs.txt");
					fw.write(contentOfFile);
					fw.close();	
				
					
					Collections.sort(list,new sortSongs());
					
					obsList = FXCollections.observableArrayList(list); 

					listView.setItems(obsList); 
					
			}
			save.setVisible(false);
			cancelEdit.setVisible(false);
		}else {
			cancel();
		}
		songDetailV2();
	}
	public void cancel(ActionEvent e) {
		nameDet.setEditable(false);
		artistDet.setEditable(false);
		albumDet.setEditable(false);
		yearDet.setEditable(false);
		listView.setMouseTransparent(false);
		listView.setFocusTraversable(true);
		del.setDisable(false);
		add.setDisable(false);
		edit.setDisable(false);
		cancelAdd.setVisible(false);
		cancelEdit.setVisible(false);
		saveAdd.setVisible(false);
		save.setVisible(false);
		songDetailV2();
	}
	public void cancel() { //override
		nameDet.setEditable(false);
		artistDet.setEditable(false);
		albumDet.setEditable(false);
		yearDet.setEditable(false);
		listView.setMouseTransparent(false);
		listView.setFocusTraversable(true);
		del.setDisable(false);
		add.setDisable(false);
		edit.setDisable(false);
		cancelAdd.setVisible(false);
		cancelEdit.setVisible(false);
		saveAdd.setVisible(false);
		save.setVisible(false);
		songDetailV2();
	}
	
	
	class sortSongs implements Comparator<String> 
	{ 
	    public int compare(String a, String b) 
	    { 

	       String[] aDivide=a.split("|",2);
	       String[] bDivide=b.split("|",2);

	       String firstSong=aDivide[0].trim();
	       String secondSong=bDivide[0].trim();
	       int cmp=firstSong.compareToIgnoreCase(secondSong);
	       if (cmp!=0) {
	    	   return cmp;
	       }
	       return aDivide[1].trim().compareToIgnoreCase(bDivide[1].trim()); 
	    } 
	} 
	public static boolean isInteger(String s) {  
	    try { 
	        Integer.parseInt(s); 
	        return true; 
	    } 
	    catch (Exception e){  
	        return false; 
	    } 
	}
	public void clearText() {
		nameDet.clear();
		artistDet.clear();
		albumDet.clear();
		yearDet.clear();
	}
	  
}