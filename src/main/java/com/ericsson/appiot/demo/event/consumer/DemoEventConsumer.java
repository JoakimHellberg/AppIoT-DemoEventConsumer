package com.ericsson.appiot.demo.event.consumer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

import com.ericsson.appiot.demo.event.consumer.model.UIEventMessage;
import com.google.gson.Gson;

import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import se.sigma.sensation.event.sdk.dto.IntegrationTicket;

@SuppressWarnings({ "unchecked", "rawtypes", "restriction"})
public class DemoEventConsumer extends Application {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	public static final String FILE_INTEGRATION_TICKET = "IntegrationTicket.json";
	private MyEventProcessor eventProcessor;	
	private TableView<UIEventMessage> table = new TableView<UIEventMessage>();
	private final ObservableList<UIEventMessage> eventList = FXCollections.observableArrayList();
	        
	public static void main(String[] args) {
		launch(args);
	}

	public void start(final Stage primaryStage) {
		primaryStage.setTitle("DEMO Event Consumer");
        primaryStage.show();

        BorderPane pane = new BorderPane();
        pane.setCenter(table);
        
        Text scenetitle = new Text("GENERATED EVENTS");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        scenetitle.setFill(Color.WHITE);
        Button btn = new Button("Import Integration Ticket.");
        
        StackPane stackPane = new StackPane();
        stackPane.setAlignment(Pos.CENTER_RIGHT); 
        stackPane.getChildren().add(btn);

        HBox hbox = new HBox();
        hbox.setStyle("-fx-background-color: #336699;");
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);
        hbox.getChildren().addAll(scenetitle, stackPane);
        HBox.setHgrow(stackPane, Priority.ALWAYS);
        
        pane.setTop(hbox);

        
        btn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Select Integration Ticket");
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("JSON", "*.json"),
                        new FileChooser.ExtensionFilter("All Files", "*.*")
                    );
                File file = fileChooser.showOpenDialog(primaryStage);
                if (file != null) {
                	FileReader fr;
					try {
						fr = new FileReader(file);
	                    BufferedReader in = new BufferedReader(fr);
	                    String ticket = in.readLine();
	                    IntegrationTicket integrationTicket = new Gson().fromJson(ticket, IntegrationTicket.class);
	                    in.close();
	                    
	                    saveIntegrationTicket(integrationTicket);
	                    if(eventProcessor != null) {
	                    	eventProcessor.stop();
	                    	eventProcessor = null;
	                    }
	                    eventProcessor = new MyEventProcessor(integrationTicket, eventList);
	                    eventProcessor.start();
					} catch (Exception e) {
						e.printStackTrace();
					}                     
                }
            }
        });
        
        final Label label = new Label("Generated events by AppIoT ");
        label.setFont(new Font("Arial", 20)); 
        table.setEditable(false);        
        
        TableColumn<UIEventMessage, String> timeCol = new TableColumn<UIEventMessage, String>("Time");
        timeCol.setMinWidth(150);
        timeCol.setCellValueFactory(new PropertyValueFactory("time"));
        TableColumn<UIEventMessage, String> locationCol = new TableColumn<UIEventMessage, String>("Location");
        locationCol.setCellValueFactory(new PropertyValueFactory("location"));
        locationCol.setMinWidth(150);
        TableColumn<UIEventMessage, String> deviceNameCol = new TableColumn<UIEventMessage, String>("Device");
        deviceNameCol.setCellValueFactory(new PropertyValueFactory("device"));
        deviceNameCol.setMinWidth(150);
        TableColumn<UIEventMessage, String> sensorNameCol = new TableColumn<UIEventMessage, String>("Sensor");
        sensorNameCol.setCellValueFactory(new PropertyValueFactory("sensor"));
        sensorNameCol.setMinWidth(150);
        TableColumn<UIEventMessage, String> measurementCol = new TableColumn<UIEventMessage, String>("Measurement");
        measurementCol.setCellValueFactory(new PropertyValueFactory("measurement"));
        measurementCol.setMinWidth(150);        
        
        TableColumn<UIEventMessage, Boolean> btnCol = new TableColumn<>("Reset");
        btnCol.setMinWidth(150);
        btnCol.setSortable(false);
     
        // define a simple boolean cell value for the action column so that the column will only be shown for non-empty rows.
        btnCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<UIEventMessage, Boolean>, ObservableValue<Boolean>>() {
          @Override public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<UIEventMessage, Boolean> features) {
            return new SimpleBooleanProperty(features.getValue() != null);
          }
        });
        
     // create a cell value factory with an add button for each row in the table.
        btnCol.setCellFactory(new Callback<TableColumn<UIEventMessage, Boolean>, TableCell<UIEventMessage, Boolean>>() {
          @Override public TableCell<UIEventMessage, Boolean> call(TableColumn<UIEventMessage, Boolean> personBooleanTableColumn) {
            return new ResetEventCell(primaryStage, table);
          }
        });
        
        table.setItems(eventList);        
        table.getColumns().addAll(timeCol, locationCol, deviceNameCol, sensorNameCol, measurementCol, btnCol);
        
        
        Scene scene = new Scene(pane, 1024, 800);
        primaryStage.setScene(scene);
        
        try {
            IntegrationTicket integrationTicket = getIntegrationTicket();
            if(integrationTicket != null) {
            	eventProcessor = new MyEventProcessor(integrationTicket, eventList);
                eventProcessor.start();
            }        	
        } catch (Exception e) {
        	logger.info("No integration ticket found.");
        }    
	}
	
	public IntegrationTicket getIntegrationTicket() throws IOException {
		IntegrationTicket result = null;
		try {
			File ticketFile = new File(FILE_INTEGRATION_TICKET);
			if(!ticketFile.exists()) {
				return null;
			}
			FileReader fr = new FileReader(ticketFile);
            BufferedReader in = new BufferedReader(fr);
            String ticket = in.readLine();
            result = new Gson().fromJson(ticket, IntegrationTicket.class);
            in.close();
        }
        catch (Exception e) {
        }
		return result;
	}
	
	public void saveIntegrationTicket(IntegrationTicket integrationTicket) throws IOException {
		File ticketFile = new File(FILE_INTEGRATION_TICKET);
        logger.info("Saving IntegrationTicket to file : " + ticketFile.getAbsolutePath());

		if(ticketFile.exists()) {
			logger.info("IntegrationTicket already exists, deleting existing.");
			ticketFile.delete();
		}
		
		FileWriter fw = new FileWriter(ticketFile);
        String ticket = new Gson().toJson(integrationTicket);
        fw.write(ticket);
        fw.close();
	}	

	/** A table cell containing a button for adding a new person. */
	  private class ResetEventCell extends TableCell<UIEventMessage, Boolean> {
	    final Button resetButton       = new Button("Reset");
	    final StackPane paddedButton = new StackPane();


	    ResetEventCell(final Stage stage, final TableView table) {
	      paddedButton.setPadding(new Insets(3));
	      paddedButton.getChildren().add(resetButton);
	      resetButton.setOnMousePressed(new EventHandler<MouseEvent>() {
	        @Override 
	        public void handle(MouseEvent mouseEvent) {
	          
	        }
	      });
	      resetButton.setOnAction(new EventHandler<ActionEvent>() {
	        @Override 
	        public void handle(ActionEvent actionEvent) {
	          eventProcessor.reset(getTableRow().getIndex());
	          table.getSelectionModel().select(getTableRow().getIndex());
	        }
	      });
	    }

	    /** places an add button in the row only if the row is not empty. */
	    @Override protected void updateItem(Boolean item, boolean empty) {
	      super.updateItem(item, empty);
	      if (!empty) {
	        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
	        setGraphic(paddedButton);
	      }
	    }
	  }
}
