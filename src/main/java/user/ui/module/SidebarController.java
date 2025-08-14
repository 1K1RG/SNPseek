package user.ui.module;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.irri.iric.ds.chado.domain.model.User;
import org.irri.iric.portal.AppContext;
import org.irri.iric.portal.WebConstants;
import org.irri.iric.portal.admin.WorkspaceFacade;
import org.irri.iric.portal.admin.WorkspaceLoadLocal;
import org.irri.iric.portal.genotype.GenotypeFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkmax.zul.Nav;
import org.zkoss.zkmax.zul.Navbar;
import org.zkoss.zkmax.zul.Navitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.Include;

import user.ui.module.util.constants.ContentConstants;
import user.ui.module.util.constants.SessionConstants;
import user.ui.module.util.constants.UserConstants;

public class SidebarController extends SelectorComposer<Component> {

	private Session sess;

	@Wire
	private Navbar navbar;

	@Wire
	private Nav search;

	@Wire
	private Include contentInclude;

	@Wire
	private Div container;

	@Wire
	private Navitem collapseId;

	@Wire
	private Navitem jbrowse;

	@Wire
	private Navitem jbrowse2;

	@Wire
	private Navitem gwas;

	@Wire
	private Navitem download;

	@Wire
	private Navitem myList;

	@Wire
	private Navitem orderSeeds;

	@Wire
	private Navitem traitGenes;

	private User user;

	private String paramstr;

	@Autowired
	@Qualifier("GenotypeFacade")
	private GenotypeFacade genotype;

	@Autowired
	@Qualifier("WorkspaceFacade")
	private WorkspaceFacade workspace;

	private String page;

	private String src;

	private String from;

	private String phenotype;

	@Override
	public void doAfterCompose(Component comp) throws Exception {

		super.doAfterCompose(comp);

		String message = (String) Sessions.getCurrent().getAttribute(SessionConstants.NOTIFICATION);

		if (message != null) {
			Clients.showNotification(message, "info", null, "middle_center", 10000, true);
			// Optionally, clear the attribute to prevent it from showing multiple times
			Sessions.getCurrent().removeAttribute(SessionConstants.NOTIFICATION);
		}

		Session sess = Sessions.getCurrent();
		user = (User) sess.getAttribute(SessionConstants.USER_CREDENTIAL);

		paramstr = AppContext.getJBrowseDefaulttracks(AppContext.getDefaultDataset());

		genotype = (GenotypeFacade) AppContext.checkBean(genotype, "GenotypeFacade");
		workspace = (WorkspaceFacade) AppContext.checkBean(workspace, "WorkspaceFacade");

		AppContext.debug(AppContext.getFlatfilesDir() + "preloadSNPS/");
		WorkspaceLoadLocal.loadSNPLocalFile(AppContext.getFlatfilesDir() + "/preloadSNPS/", workspace, genotype);

		if (user != null) {
			File directory = new File(AppContext.getFlatfilesDir() + WebConstants.USER_DIR + File.separator
					+ user.getEmail() + File.separator + WebConstants.SNP_DIR);
			WorkspaceLoadLocal.loadSNPLocalFile(directory.getAbsolutePath(), workspace, genotype);
			WorkspaceLoadLocal.initUserVarietyList(user.getEmail(), workspace);
			WorkspaceLoadLocal.initUserList(WebConstants.LOCUS_DIR, user.getEmail(), workspace);
			
		}
//		sess = Sessions.getCurrent();
//		user = (User) sess.getAttribute(SessionConstants.USER_CREDENTIAL);

		Properties prop = (Properties) sess.getAttribute(SessionConstants.CONTENT_MANAGER);

		if (prop == null) {
			prop = new Properties();
			InputStream contentManager = AppContext.class
					.getResourceAsStream("/ACCESS_" + UserConstants.ANONYMOUS + ".properties");
			try {

				prop.load(contentManager);

			} catch (IOException e) {

				e.printStackTrace();
			}

		}

		download.setVisible(Boolean.valueOf(prop.get(ContentConstants.SIDEBAR_DOWNLOAD).toString()));
		gwas.setVisible(Boolean.valueOf(prop.get(ContentConstants.SIDEBAR_GWAS).toString()));
		myList.setVisible(Boolean.valueOf(prop.get(ContentConstants.SIDEBAR_MYLIST).toString()));
		jbrowse.setVisible(Boolean.valueOf(prop.get(ContentConstants.SIDEBAR_JBROWSE).toString()));
		jbrowse2.setVisible(Boolean.valueOf(prop.get(ContentConstants.SIDEBAR_JBROWSE2).toString()));
		orderSeeds.setVisible(Boolean.valueOf(prop.get(ContentConstants.SIDEBAR_ORDERSEEDS).toString()));
		traitGenes.setVisible(Boolean.valueOf(prop.get(ContentConstants.SIDEBAR_TRAIT_GENES).toString()));

		contentInclude.setSclass("content");

		navbar.setCollapsed(false);

		page = Executions.getCurrent().getParameter("page");
		src = Executions.getCurrent().getParameter("src");
		from = Executions.getCurrent().getParameter("from");
		phenotype = Executions.getCurrent().getParameter("phenotype");

		if (page != null) {

			AppContext.debug("from params.. " + page);
			if (page.equals("workspace.zul")) {
				myList();

			}

		}

	}

	@Listen("onClick =#menuitem_user")
	public void onbutton_addUser() {

		Session sess = Sessions.getCurrent();

		sess.removeAttribute("userCredential");

		Executions.sendRedirect("index.zul");

	}

	@Listen("onClick =#searchGenotype")
	public void searchGenotype() {
		collapsedSidebar();
		contentInclude.setSrc("/genotypeContent.zul");

	}
	
	@Listen("onClick =#1k1")
	public void search1k1() {
		collapsedSidebar();
		contentInclude.setSrc("/1k1Content.zul");

	}

	private void collapsedSidebar() {
		navbar.setCollapsed(true);
		contentInclude.setSclass("content collapsed");

	}

	@Listen("onClick =#searchVariety")
	public void searchVariety() {
		collapsedSidebar();
		contentInclude.setSrc("/varietiesContent.zul");
	}

	@Listen("onClick =#searchGeneLoci")
	public void searchGeneLoci() {
		collapsedSidebar();
		contentInclude.setSrc("/geneLociContent.zul");
	}

	@Listen("onClick =#jbrowse")
	public void jbrowse() {
		collapsedSidebar();
		contentInclude.setSrc("/jbrowse.zul");
	}

	@Listen("onClick =#jbrowse2")
	public void jbrowse2() {
		collapsedSidebar();
		contentInclude.setSrc("/_jbrowse2.zul");
	}

	@Listen("onClick =#gwas")
	public void gwas() {
		collapsedSidebar();
		contentInclude.setSrc("/gwas.zul");
	}

	@Listen("onClick =#traitGenes")
	public void traitGenes() {
		collapsedSidebar();
		String paramstr = AppContext.getJBrowseDefaulttracks(AppContext.getDefaultDataset());
		// contentInclude.setSrc("/_ideo.zul?tracks=${paramstr}&amp;app=rice-ideogram");
		contentInclude.setSrc("/traitgenes.zul");

	}

	@Listen("onClick =#myList")
	public void myList() {
		collapsedSidebar();
		if (src != null && from != null)
			contentInclude.setSrc("/MyListContent.zul?from=" + from + "&src" + src);
		else
			contentInclude.setSrc("/MyListContent.zul");
	}

	@Listen("onClick =#orderSeeds")
	public void orderSeeds() {
		collapsedSidebar();
		contentInclude.setSrc("/OrderContent.zul");
	}

	@Listen("onClick =#download")
	public void download() {
		collapsedSidebar();
		contentInclude.setSrc("/downloadContent.zul");
	}

	@Listen("onClick =#collapseId")
	public void onCollapse() {
		if (navbar.isCollapsed()) {
			navbar.setCollapsed(false);
			contentInclude.setSclass("content");
		} else {
			navbar.setCollapsed(true);
			contentInclude.setSclass("content collapsed");
		}
	}

}
