package user.ui.module;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;



@Controller
@Scope("session")
public class TimeoutController extends SelectorComposer<Component> {
	
	@Wire
	private Label lbl_time;
	

	public void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);
		
		
		for (int i=5; i>=0 ; i--) {
			lbl_time.setValue(i+"");
			lbl_time.invalidate();
			Thread.sleep(1000);
		}
		
		Executions.sendRedirect("index.zul");
	}

}
