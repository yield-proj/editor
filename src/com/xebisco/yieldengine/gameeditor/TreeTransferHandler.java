package com.xebisco.yieldengine.gameeditor;

import com.xebisco.yieldengine.core.EntityFactory;
import com.xebisco.yieldengine.shipruntime.PreMadeEntityFactory;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.List;

public class TreeTransferHandler extends TransferHandler {
    DataFlavor nodesFlavor;
    DataFlavor[] flavors = new DataFlavor[1];
    DefaultMutableTreeNode[] nodesToRemove;
    int lastAction;

    public TreeTransferHandler() {
        try {
            String mimeType = DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" +
                    DefaultMutableTreeNode[].class.getName() + "\"";
            nodesFlavor = new DataFlavor(mimeType);
            flavors[0] = nodesFlavor;
        } catch (ClassNotFoundException e) {
            System.out.println("ClassNotFound: " + e.getMessage());
        }
    }

    private boolean haveCompleteNode(JTree tree) {
        int[] selRows = tree.getSelectionRows();
        TreePath path = tree.getPathForRow(selRows[0]);
        DefaultMutableTreeNode first =
                (DefaultMutableTreeNode) path.getLastPathComponent();
        int childCount = first.getChildCount();
        // first has children and no children are selected.
        if (childCount > 0 && selRows.length == 1)
            return false;
        // first may have children.
        for (int i = 1; i < selRows.length; i++) {
            path = tree.getPathForRow(selRows[i]);
            DefaultMutableTreeNode next =
                    (DefaultMutableTreeNode) path.getLastPathComponent();
            if (first.isNodeChild(next)) {
                // Found a child of first.
                if (childCount > selRows.length - 1) {
                    // Not all children of first are selected.
                    return false;
                }
            }
        }
        return true;
    }

    //TransferHandler
    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.COPY_OR_MOVE;
    }

    //TransferHandler
    @Override
    public boolean canImport(JComponent comp, DataFlavor flavor[]) {
        for (int i = 0, n = flavor.length; i < n; i++) {
            for (int j = 0, m = flavors.length; j < m; j++) {
                if (flavor[i].equals(flavors[j])) {
                    return true;
                }
            }
        }
        return false;
    }

    //TransferHandler
    @Override
    protected Transferable createTransferable(JComponent c) {
        JTree tree = (JTree) c;
        TreePath[] paths = tree.getSelectionPaths();
        if (paths != null) {
            List<DefaultMutableTreeNode> copies = new ArrayList<>();
            List<DefaultMutableTreeNode> toRemove = new ArrayList<>();
            DefaultMutableTreeNode node =
                    (DefaultMutableTreeNode) paths[0].getLastPathComponent();
            DefaultMutableTreeNode copy = copy(node);
            copies.add(copy);
            toRemove.add(node);
            for (int i = 1; i < paths.length; i++) {
                DefaultMutableTreeNode next =
                        (DefaultMutableTreeNode) paths[i].getLastPathComponent();
                // Do not allow higher level nodes to be added to list.
                if (next.getLevel() < node.getLevel()) {
                    break;
                } else if (next.getLevel() > node.getLevel()) {  // child node
                    copy.add(copy(next));
                    // node already contains child
                } else {                                        // sibling
                    copies.add(copy(next));
                    toRemove.add(next);
                }
            }
            DefaultMutableTreeNode[] nodes =
                    copies.toArray(new DefaultMutableTreeNode[copies.size()]);
            nodesToRemove =
                    toRemove.toArray(new DefaultMutableTreeNode[toRemove.size()]);
            return new NodesTransferable(nodes);
        }
        return null;
    }

    /**
     * Defensive copy used in createTransferable.
     */
    private DefaultMutableTreeNode copy(TreeNode node) {
        Object o = (((DefaultMutableTreeNode) node).getUserObject());
        if (o instanceof PreMadeEntityFactory f)
            o = f.clone();
        return new DefaultMutableTreeNode(o);
    }

    @Override
    public void exportToClipboard(JComponent comp, Clipboard clip, int action) throws IllegalStateException {
        super.exportToClipboard(comp, clip, action);
        lastAction = action;
    }

    @Override
    public void exportAsDrag(JComponent comp, InputEvent e, int action) {
        super.exportAsDrag(comp, e, action);
        lastAction = action;
    }

    private void updateScene(EntityListEditor.EntityTree et, DefaultMutableTreeNode root) {
        et.getScene().getEntityFactories().clear();

        int index = 0;
        for (int i = 0; i < root.getChildCount(); i++) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) root.getChildAt(i);
            EntityFactory fac2 = (EntityFactory) node.getUserObject();
            if (fac2 instanceof PreMadeEntityFactory f2) {
                resetA(node, f2);
                f2.setPreferredIndex(index++);
                f2.setParent(null);
            }
            et.getScene().getEntityFactories().add(fac2);
        }
    }

    protected void exportDone2(JComponent source, int action) {
        if ((action & MOVE) == MOVE) {
            JTree tree = (JTree) source;
            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            // Remove nodes saved in nodesToRemove in createTransferable.
            for (int i = 0; i < nodesToRemove.length; i++) {
                model.removeNodeFromParent(nodesToRemove[i]);
            }
        }
        if (source instanceof EntityListEditor.EntityTree et) {
            DefaultTreeModel model = (DefaultTreeModel) et.getModel();
            DefaultMutableTreeNode cloned1 = cloned;
            DefaultMutableTreeNode cloned2 = (DefaultMutableTreeNode) model.getRoot();
            Main.aa(new Main.AppAction(
                    "Tree Update",
                    () -> {
                        updateScene(et, (DefaultMutableTreeNode) cloned2.getRoot());
                        et.reset();
                        Inspector.set(null);

                    },
                    () -> {
                        updateScene(et, (DefaultMutableTreeNode) cloned1.getRoot());
                        et.reset();
                        Inspector.set(null);
                    }
            ));
        }
    }


    public void resetA(DefaultMutableTreeNode root, PreMadeEntityFactory factory) {
        factory.setParent(null);
        factory.getChildren().clear();
        int index = 0;
        for (int i = 0; i < root.getChildCount(); i++) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) root.getChildAt(i);
            EntityFactory fac2 = (EntityFactory) node.getUserObject();
            if (fac2 instanceof PreMadeEntityFactory f2) {
                resetA(node, f2);
                f2.setPreferredIndex(index++);
                f2.setParent(factory);
            }
            factory.getChildren().add((EntityFactory) fac2);
        }
    }

    public DefaultMutableTreeNode cloneNode(DefaultMutableTreeNode node) {
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(node.getUserObject());
        for (int iChildren = node.getChildCount(), i = 0; i < iChildren; i++) {
            newNode.add(cloneNode((DefaultMutableTreeNode) node.getChildAt(i)));
        }
        return newNode;
    }

    private DefaultMutableTreeNode cloned;

    //TransferHandler
    @Override
    public boolean importData(TransferHandler.TransferSupport support) {
        if (!canImport(support)) {
            return false;
        }

        // Extract transfer data.
        DefaultMutableTreeNode[] nodes = null;
        try {
            Transferable t = support.getTransferable();
            nodes = (DefaultMutableTreeNode[]) t.getTransferData(nodesFlavor);
        } catch (UnsupportedFlavorException ufe) {
            System.out.println("UnsupportedFlavor: " + ufe.getMessage());
        } catch (java.io.IOException ioe) {
            System.out.println("I/O error: " + ioe.getMessage());
        }
        // Get drop location info.
        int childIndex;
        TreePath dest;
        if (support.isDrop()) {
            JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
            childIndex = dl.getChildIndex();
            dest = dl.getPath();
        } else {
            childIndex = -1;
            JTree tree = (JTree) support.getComponent();
            dest = tree.getSelectionPath();
        }
        DefaultMutableTreeNode parent
                = (DefaultMutableTreeNode) dest.getLastPathComponent();
        JTree tree = (JTree) support.getComponent();
        cloned = cloneNode((DefaultMutableTreeNode) tree.getModel().getRoot());
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        // Configure for drop mode.
        int index = childIndex;    // DropMode.INSERT
        if (childIndex == -1) {     // DropMode.ON
            index = parent.getChildCount();
        }
        // Add data to model.
        for (int i = 0; i < nodes.length; i++) {
            // ArrayIndexOutOfBoundsException
            model.insertNodeInto(nodes[i], parent, index++);
        }
        exportDone2(tree, lastAction);
        return true;
    }

    //TransferHandler
    @Override
    public boolean importData(JComponent comp, Transferable t) {
        return importData(new TransferHandler.TransferSupport(comp, t));
    }

    public class NodesTransferable implements Transferable {
        DefaultMutableTreeNode[] nodes;

        public NodesTransferable(DefaultMutableTreeNode[] nodes) {
            this.nodes = nodes;
        }

        //Transferable
        @Override
        public Object getTransferData(DataFlavor flavor) {
            if (!isDataFlavorSupported(flavor)) {
                return false;
            }
            return nodes;
        }

        //Transferable
        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return flavors;
        }

        //Transferable
        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavor.equals(nodesFlavor);
        }
    }
}