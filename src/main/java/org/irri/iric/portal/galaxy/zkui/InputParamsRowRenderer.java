package org.irri.iric.portal.galaxy.zkui;

import java.util.Date;

import org.irri.iric.portal.AppContext;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Fileupload;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Textbox;

public class InputParamsRowRenderer implements RowRenderer{

	

	
	@Override
	public void render(Row row, Object obj, int i) throws Exception {
		String[] params=null;
		try {
			params=(String[])obj;   //new String[] {p,paramval});

			//input id="inp0" type="text" zk:onChange="add()"
					
			//AppContext.debug("render..." + params[0] + "   " + params[1]);
			//Html h=new Html();
			
			//Input inp=new Input();
			//inp.setAttribute("type","file");
			//inp.adda
			//Textbox tb=new Textbox(params[1]); 
			//tb.setWidth("95%");
			

			Button b=new Button("...");
			b.setWidth("15px");
			b.addEventListener("onClick", new EventListener() {
	           		 public void onEvent(Event event) throws Exception {
	           			 /*
	           		 Desktop desktop = Executions.getCurrent().getDesktop(); 
					//Desktop desktop = self.getDesktop(); 
					Configuration conf = desktop.getWebApp().getConfiguration(); 
					conf.setMaxUploadSize(1024); 
					conf.setUploadCharset("gbk"); 
					//desktop.
					 * */
					 
	           			//Fileupload.get(true);
	           			try {
						Fileupload.get(new EventListener() {
	           				@Override
	           				public void onEvent(Event event) throws Exception {
	           					try {
	           						//org.zkoss.util.media.Media media = ((UploadEvent) event).getMedia();
	           						org.zkoss.util.media.Media media = ((UploadEvent) event).getMedia();
	           						media.getStringData();
	           						//tb.setValue(media.getName());
	           					} catch (Exception ex) {
	           						AppContext.debug(ex.getMessage());
	           						ex.getStackTrace();
	           						AppContext.debug("Select file failed");
	           					}
	           				}
	           			});
						
	           			} catch(Exception ex) {
	           				ex.printStackTrace();
	           				if (ex.getCause() instanceof Exception) {
	           					AppContext.debug("caused by:");
	           					((Exception)ex.getCause()).printStackTrace();
	           				}
	           			}
	           			
	           			
         	   }});

			String[] labels=params[0].split("\\:");		
			new Label(labels[0]).setParent(row);
			
			String optional="";
			String options="";
			String typestepno=labels[1];

			if(labels.length>4 && ((String)labels[4]).toLowerCase().equals("true") ) optional="(Optional)";
			if(labels.length>5 &&  typestepno.equals("select") ) {
				options= " ["+labels[5].replace("|",",")+"]";
			}

			boolean isdate=false;
			String dateformat="";
			if(labels.length>2) {
				if(labels[2].contains("|")) {
					new Label(labels[2].split("\\|")[0]).setParent(row);
					new Label(optional+" " + labels[2].split("\\|")[1]).setParent(row);
					String datelabel=labels[2].split("\\|")[1];
					isdate=datelabel.toLowerCase().contains("date (");
					if(isdate)
						dateformat=datelabel.split("\\(")[1].replace(")", "").trim();

				}
				else if(labels[2].trim().isEmpty()) { 
					new Label(labels[0].replaceAll("_\\d+$","")).setParent(row);
					new Label("").setParent(row);
				}
				else {
					new Label(labels[2]).setParent(row);
					new Label(optional).setParent(row);
				}
			}
			else if(labels.length==1) { 
				new Label(labels[0].replaceAll("_\\d+$","")).setParent(row);
				new Label("").setParent(row);

			}
			else {
				new Label("").setParent(row);
				new Label("").setParent(row);
			}
			
			

			/*
			Hbox hbox=new Hbox();
				tb.setParent(hbox);
				b.setParent(hbox);
			if(!labels[1].equals("data"))
				tb.setParent(row);
			else hbox.setParent(row); //  inp.setParent(row);			
			*/

			Component tb=null;
			
			//if(labels[1].equals("select")) {
			if(labels.length>5 && !labels[5].isEmpty()) {
				Listbox cb=new Listbox();
				cb.setParent(row);
				//cb.setWidth("370px" );
				cb.setWidth("470px" );
				cb.setMold("select");
				cb.setModel( new ListModelList(labels[5].split("\\|")));
				int selindex=0;
				int selindexcnt=0;
				for(Listitem item:cb.getItems()) {
					if(item.getLabel().trim().equals(labels[3].trim())) {
						selindex=selindexcnt;
						//cb.setSelectedItem(item);
						//cb.setReadonly(true);
						break;
					}
					selindexcnt++;
				}
				cb.setSelectedIndex(selindex);
				tb=cb;
			} else if(typestepno.equals("data") || typestepno.equals("data_input")) {
				Hbox hbox=new Hbox();
				hbox.setWidth("80%,20%");
				Textbox tbValue=new Textbox();
				tbValue.setParent(hbox);
				//tbValue.setWidth("300px" );
				tbValue.setWidth("400px" );

				Button butUpload=new Button("Upload");
				butUpload.addEventListener("onClick", new EventListener() {					
					@Override
	           		public void onEvent(Event event) throws Exception {
	           			Fileupload.get(new EventListener() {
	           				@Override
	           				public void onEvent(Event event) throws Exception {
			           			try {
			    					Media media = ((UploadEvent) event).getMedia();
				           			String fname=AppContext.uploadToServer(AppContext.getTempDir(), media);
				           			tbValue.setValue(fname);
			           			} catch(Exception ex) {
			           				ex.printStackTrace();
			           			}
	           				}
	           			});
					}
	           	});
				butUpload.setParent(hbox);
				butUpload.setWidth("75px");
				if( labels[1] .equals("data_input"))
					tbValue.setValue(  params[1].trim());
				else {
					if(labels.length>3)
						tbValue.setValue( labels[3].trim());
					else tbValue.setValue("");
				}
				hbox.setParent(row);
			} else if(isdate) {
				Datebox db=new Datebox();
				db.setCols(50);
				db.setFormat(  dateformat.replace("YYYY","yyyy").replace("YY","yy").replace("DD","dd").replace("mm","MM"));
				db.setValue(new Date());
				//<datebox id="db5" cols="12" format="yyyy/MM/dd" onCreate="self.value = new Date()" />) {
				db.setParent(row);
			}
			else {
				Textbox cb=new Textbox();
				cb.setParent(row);
				cb.setWidth("470px" );
				if( labels[1] .equals("data_input"))
					cb.setValue(  params[1].trim());
				else {
					if(labels.length>3)
						cb.setValue( labels[3].trim());
					else cb.setValue("");
				}
				
				
				/*
				if(cb.getValue().trim().isEmpty()) {
					if(labels.length>3)
						cb.setValue( labels[3] );			
					else cb.setValue("");
					cb.setReadonly(false);
				} else {
					// from embed
					cb.setReadonly(true);
				}
				*/
				
				tb=cb;
			}
			
			AppContext.debug("inputparams: params[0]=" + params[0] +  "   params[1]=" + params[1]);
			
			/*
			String optional="";
			String options="";
			String typestepno=labels[1];

			if(labels.length>4 && ((String)labels[4]).toLowerCase().equals("true") ) optional="(Optional)";
			if(labels.length>5 &&  typestepno.equals("select") ) {
				options= " ["+labels[5].replace("|",",")+"]";
			}

			if(labels.length>2) {
				new Label(optional+" " + labels[2] + options).setParent(row);
			}
			else {
				new Label(optional + options).setParent(row);
			}
			*/
			
			if(labels.length>4) typestepno+=":"+labels[4];
			else typestepno+=":";
			
			// set stepno=annot
			//String  typestepno=labels[1]+":"+labels[2];
			
			new Textbox(typestepno).setParent(row);
			
		} catch(Exception ex) {
			ex.printStackTrace();
			AppContext.debug(params[0]);
		}

	}

}
