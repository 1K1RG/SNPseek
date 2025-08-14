package org.irri.iric.portal.galaxy.zkui;

import java.io.Serializable;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.Selectors;

public class TestUpload implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;


    @AfterCompose
    public void afterCompose(
            @ContextParam(ContextType.VIEW)
            Component view) {
        Selectors.wireComponents(view, this, false);
    }

    @Command
    public void onFileUpload(@ContextParam(ContextType.BIND_CONTEXT) BindContext ctx) {
        UploadEvent upEvent = null;
        Object objUploadEvent = ctx.getTriggerEvent();

        if (objUploadEvent != null && (objUploadEvent instanceof UploadEvent)) {
            upEvent = (UploadEvent) objUploadEvent;

            Media media = upEvent.getMedia();

            //Initialize components
            String name = media.getName();
            String format = media.getFormat();

            byte[] bFile = media.getByteData();

            System.out.println(String.format("File Name: %s, Format: %s" , name, format ));
        }
    }

}