package user.ui.module;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zkmax.zul.GoldenLayout;
import org.zkoss.zkmax.zul.GoldenPanel;
import org.zkoss.zul.Label;

public class MyComposer extends SelectorComposer<Component> {

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		final GoldenLayout goldenLayout = new GoldenLayout();
		goldenLayout.setWidth("100%");
		goldenLayout.setHeight("100%");

		goldenLayout.appendChild(createPanel("A"));
		goldenLayout.appendChild(createPanel("B"));
		goldenLayout.appendChild(createPanel("C"));
		goldenLayout.appendChild(createPanel("D"));
		goldenLayout.appendChild(createPanel("E"));
		goldenLayout.appendChild(createPanel("F"));

		comp.appendChild(goldenLayout);
	}

	private GoldenPanel createPanel(String title) {
		final GoldenPanel panel = new GoldenPanel();
		panel.setTitle(title);
		panel.appendChild(new Label(title));
		return panel;
	}

}