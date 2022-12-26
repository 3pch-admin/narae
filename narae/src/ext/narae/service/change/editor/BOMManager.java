package ext.narae.service.change.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;

import ext.narae.service.change.beans.ChangeHelper;

public class BOMManager extends JPanel {
	JButton create_bt;
	JButton load_bt;
	JButton save_bt;
	JButton undo;
	JButton redo;
	JButton reload_bt;
	JButton action_bt;
	JButton preViewEulB;
	JButton partList_bt;
	JButton reference_bt;
	JButton preView_bt;

	JButton helpIcon;
	JRadioButton addChildMode;
	JRadioButton changeMode;
	JRadioButton baseCopyMode;
	public ButtonGroup group;

	public EditTreePanel editPanel;
	public SupportPanel supportPanel;

	public BOMManager(JFrame frame) {
		try {
			System.out.println("= Philosopher's stone =");
			System.out.println(" 2005.3");
			System.out.println(" YHJang");
			BEContext.setTop(frame);
			BEContext.setModel(new DataModel());
			wt.util.WTContext.init(frame);
			BEContext.handler = new EditHandler();

			// String theme = BEContext.host +
			// "com/e3ps/change/editor/gfxOasisthemepack.zip";
			// SkinLookAndFeel.setSkin(SkinLookAndFeel.loadThemePack(theme));
			// UIManager.setLookAndFeel("com.l2fprod.gui.plaf.skin.SkinLookAndFeel");

			ImageIcon ticon = new ImageIcon(new URL(BEContext.host + "com/e3ps/change/editor/images/title.gif"));
			Image icon = ticon.getImage();
			frame.setIconImage(icon);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		setLayout(new BorderLayout());
		JToolBar br = new JToolBar();
		MenuAction ma = new MenuAction();
		try {
			create_bt = new JButton(
					new ImageIcon(new URL(BEContext.host + "com/e3ps/change/editor/images/create.gif")));
			create_bt.setToolTipText("Load Ass'y ");
			create_bt.setMargin(new Insets(0, 0, 0, 0));
			create_bt.addActionListener(ma);
			br.add(create_bt);

			load_bt = new JButton(new ImageIcon(new URL(BEContext.host + "com/e3ps/change/editor/images/load.gif")));
			load_bt.setToolTipText("Load EulB");
			load_bt.setMargin(new Insets(0, 0, 0, 0));
			load_bt.addActionListener(ma);
			br.add(load_bt);

			reload_bt = new JButton(
					new ImageIcon(new URL(BEContext.host + "com/e3ps/change/editor/images/change.gif")));
			reload_bt.setToolTipText("Reload");
			reload_bt.setMargin(new Insets(0, 0, 0, 0));
			reload_bt.addActionListener(ma);
			br.add(reload_bt);

			br.addSeparator();

			undo = new JButton(new ImageIcon(new URL(BEContext.host + "com/e3ps/change/editor/images/undo.gif")));
			undo.setToolTipText("Undo");
			undo.setMargin(new Insets(0, 0, 0, 0));
			undo.addActionListener(ma);
			br.add(undo);

			redo = new JButton(new ImageIcon(new URL(BEContext.host + "com/e3ps/change/editor/images/redo.gif")));
			redo.setToolTipText("Redo");
			redo.setMargin(new Insets(0, 0, 0, 0));
			redo.addActionListener(ma);
			br.add(redo);

			save_bt = new JButton(new ImageIcon(new URL(BEContext.host + "com/e3ps/change/editor/images/save.gif")));
			save_bt.setToolTipText("Save");
			save_bt.setMargin(new Insets(0, 0, 0, 0));
			save_bt.addActionListener(ma);
			br.add(save_bt);

			br.addSeparator();

			partList_bt = new JButton(
					new ImageIcon(new URL(BEContext.host + "com/e3ps/change/editor/images/partlist.gif")));
			partList_bt.setToolTipText("품목 리스트");
			partList_bt.setMargin(new Insets(0, 0, 0, 0));
			partList_bt.addActionListener(ma);
			br.add(partList_bt);

			reference_bt = new JButton(
					new ImageIcon(new URL(BEContext.host + "com/e3ps/change/editor/images/bom.gif")));
			reference_bt.setToolTipText("참고 BOM");
			reference_bt.setMargin(new Insets(0, 0, 0, 0));
			reference_bt.addActionListener(ma);
			br.add(reference_bt);

			preView_bt = new JButton(
					new ImageIcon(new URL(BEContext.host + "com/e3ps/change/editor/images/changeview.gif")));
			preView_bt.setToolTipText("변경사항 미리보기");
			preView_bt.setMargin(new Insets(0, 0, 0, 0));
			preView_bt.addActionListener(ma);
			br.add(preView_bt);

			br.addSeparator();

			preViewEulB = new JButton(
					new ImageIcon(new URL(BEContext.host + "com/e3ps/change/editor/images/html_part.gif")));
			preViewEulB.setToolTipText("웹 페이지로 확인");
			preViewEulB.setMargin(new Insets(0, 0, 0, 0));
			preViewEulB.addActionListener(ma);
//            br.add(preViewEulB);

			action_bt = new JButton(new ImageIcon(new URL(BEContext.host + "com/e3ps/change/editor/images/apply.gif")));
			action_bt.setToolTipText("데이타베이스 적용 (어드민 전용)");
			action_bt.setMargin(new Insets(0, 0, 0, 0));
			action_bt.addActionListener(ma);
			if (BEContext.isAdmin()) {
				// br.add(action_bt);
			}

			helpIcon = new JButton(new ImageIcon(new URL(BEContext.host + "com/e3ps/change/editor/images/help.gif")));
			helpIcon.setToolTipText("Help");
			helpIcon.setMargin(new Insets(0, 0, 0, 0));
			helpIcon.addActionListener(ma);
			br.add(helpIcon);

			br.setEnabled(true);
			br.setBackground(new Color(128, 128, 225));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		add(br, BorderLayout.NORTH);

		JPanel conPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		addChildMode = new JRadioButton("Add Child Mode");
		changeMode = new JRadioButton("Change Mode");
		baseCopyMode = new JRadioButton("Base Copy Mode");
		group = new ButtonGroup();
		group.add(addChildMode);
		group.add(changeMode);
		group.add(baseCopyMode);
		addChildMode.setSelected(true);
		conPanel.add(addChildMode);
		conPanel.add(changeMode);
		conPanel.add(baseCopyMode);

		JPanel panel = new JPanel();
		editPanel = new EditTreePanel();
		supportPanel = new SupportPanel();

		JSplitPane m_pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, editPanel, supportPanel);
		m_pane.setDividerSize(2);
		m_pane.setDividerLocation(280);
		m_pane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "e3PS BOM EDITOR"));
		panel.setLayout(new BorderLayout());
		panel.add(conPanel, BorderLayout.NORTH);
		panel.add(m_pane, BorderLayout.CENTER);
		BEStateBar bes = new BEStateBar();
		panel.add(bes, BorderLayout.SOUTH);
		add(panel, BorderLayout.CENTER);
		BEContext.setStateBar(bes);
		BEContext.setPanel(this);
		BEContext.getTop().setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	}

	public class MenuAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Component c = (Component) e.getSource();

			if (c == create_bt) {
				try {
					BOMSearchDialog spenel = new BOMSearchDialog(BEContext.getTop(), "bom search", BEContext.getView());
					spenel.createDialog();
				} catch (Exception e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(BEContext.getTop(), ((Throwable) e1).getLocalizedMessage());
				}
			} else if (c == load_bt) {
				try {
					EulSearchDialog spenel = new EulSearchDialog(BEContext.getTop(), "eul search", BEContext.getView());
					spenel.createDialog();
				} catch (Exception e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(BEContext.getTop(), ((Throwable) e1).getLocalizedMessage());
				}
			} else if (c == save_bt) {
				try {
					if (BEContext.model == null || BEContext.model.getRoot() == null)
						return;
					if (JOptionPane.showConfirmDialog(BEContext.getTop(), "변경사항을 저장합니다.", "Commit",
							JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						BEContext.saveEul();

						JOptionPane.showMessageDialog(BEContext.getTop(), "저장 되었습니다.");
					} else {
						return;
					}
				} catch (Exception e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(BEContext.getTop(), ((Throwable) e1).getLocalizedMessage());
				}

			} else if (c == reload_bt) {
				try {
					if (JOptionPane.showConfirmDialog(BEContext.getTop(), "작성된 내용이 사라집니다.", "Reload",
							JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						if (BEContext.model == null || BEContext.model.getRoot() == null)
							return;
						PartData pd = (PartData) BEContext.model.getRoot().getUserObject();
						BEContext.editor.setTree(pd.part);
					} else {
						return;
					}
				} catch (Exception e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(BEContext.getTop(), ((Throwable) e1).getLocalizedMessage());
				}
			} else if (c == undo) {
				try {
					if (BEContext.model == null || BEContext.model.getRoot() == null)
						return;
					BEContext.undo();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			} else if (c == redo) {
				try {
					if (BEContext.model == null || BEContext.model.getRoot() == null)
						return;
					BEContext.redo();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			} else if (c == action_bt) {
				try {
					if (BEContext.model == null || BEContext.model.getRoot() == null)
						return;
					if (JOptionPane.showConfirmDialog(BEContext.getTop(),
							"변경 사항을 실제 서버에 적용 시킵니다.\n적용된 변경사항은 복구 할 수 없습니다.", "Commit",
							JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						// BEContext.saveAction();
						ChangeHelper.manager.saveEulData(BEContext.eulb);
						// BEContext.bomViewCopy();
						// BEContext.createBaseline();
					} else {
						return;
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			} else if (c == preViewEulB) {
				try {
					if (BEContext.model == null || BEContext.model.getRoot() == null)
						return;
					String url = BEContext.getEulBViewURL();
					if (url == null) {
						JOptionPane.showMessageDialog(BEContext.getTop(), "먼저 저장 버튼을 눌러 주십시오");
						return;
					}
					wt.util.WTContext.getContext().showDocument(new URL(BEContext.host + url), "_Blank");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			} else if (c == partList_bt) {
				try {
					BEContext.support.setList();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			} else if (c == reference_bt) {
				try {
					BEContext.support.setTreePanel();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			} else if (c == preView_bt) {
				try {
					BEContext.support.setPreview();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			} else if (c == helpIcon) {
				try {
					HelpDialog spenel = new HelpDialog(BEContext.getTop(),
							"Eul Editor Version " + BEContext.version + " 도움말");
					spenel.createDialog();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
	};
};
