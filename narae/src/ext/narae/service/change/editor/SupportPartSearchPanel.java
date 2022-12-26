package ext.narae.service.change.editor;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import wt.part.WTPart;
import wt.vc.views.View;

public class SupportPartSearchPanel extends JPanel
{
    SelectPartPanel table_p;
    Progress progress;
    String mode;
    boolean command = false;

    public SupportPartSearchPanel(View view)
    {
        setLayout(new BorderLayout());
        progress = BEContext.progress;
        table_p = new SelectPartPanel(progress, view);
        table_p.m_table.addMouseListener(new MouseHandle());
        add(BorderLayout.CENTER, table_p);
    }

    public class MouseHandle extends MouseAdapter
    {
        public void mouseClicked(MouseEvent e)
        {
            if (e.getClickCount() == 2)
            {
                if (command)
                    return;
                int row = table_p.m_table.getSelectedRow();
                if (row < 0)
                    return;
                final WTPart epm = table_p.getEPM(row);
                Thread thread = new Thread() {
                    public void run()
                    {
                        try
                        {
                            progress.run("Loading...");
                            command = true;
                            ReferenceTreePanel panel = new ReferenceTreePanel();
                            panel.setTree(epm);
                            BEContext.support.rtree.createInternalFrame(panel, epm.getNumber() + " (" + epm.getName()
                                    + ")");
                            BEContext.support.setTreePanel();
                            command = false;
                        }
                        catch (Exception eo)
                        {
                            command = false;
                            eo.printStackTrace();
                            JOptionPane.showMessageDialog(BEContext.getTop(), ((Throwable) eo).getLocalizedMessage());
                        }
                        progress.stop();
                    }
                };
                thread.start();
            }
        }
    }
}
