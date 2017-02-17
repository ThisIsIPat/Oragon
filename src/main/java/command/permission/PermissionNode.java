package command.permission;

import java.util.HashSet;
import java.util.Set;

public class PermissionNode {
    private PermissionNode parent;
    private String name;
    private Set<PermissionNode> subNodes;
    
    public PermissionNode(String name) {
        subNodes = new HashSet<>();
        final PermissionNode directChild;
        if (name.contains(".")) {
            final String[] names = name.split("\\.");
            name = names[0];
            directChild = new PermissionNode(names[1]);
            PermissionNode buffer = directChild;
            for (int nameIt = 2; nameIt < names.length; nameIt++) {
                final PermissionNode preBuffer = new PermissionNode(names[nameIt]);
                buffer.insert(preBuffer);
                buffer = preBuffer;
            }
        } else {
            directChild = null;
        }
        this.name = name;
        
        if (directChild != null)
            insert(directChild);
    }
    
    public boolean insert(PermissionNode node) {
        if (subNodes.contains(node)) return false;
        if (node.hasParent() && !node.getParent().remove(node)) return false;
        subNodes.add(node);
        node.setParent(this);
        return true;
    }
    
    public boolean remove(PermissionNode node) {
        return subNodes.remove(node);
    }
    
    public boolean remove(String nodeName) {
        for (PermissionNode node : subNodes)
            if (node.getName().equals(nodeName))
                return subNodes.remove(node);
        return false;
    }
    
    public void setParent(PermissionNode parent) {
        this.parent = parent;
    }
    
    public PermissionNode getParent() {
        return this.parent;
    }
    
    public boolean hasParent() {
        return this.parent != null;
    }
    
    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        final StringBuilder permissionNameBuilder = new StringBuilder(this.getName());
        if (this.hasParent())
            for (PermissionNode inheritIt = this.getParent(); inheritIt != null; inheritIt = inheritIt.getParent()) {
                permissionNameBuilder.insert(0, ".");
                permissionNameBuilder.insert(0, inheritIt.getName());
            }
        return super.toString()+": \""+permissionNameBuilder.toString()+"\"";
    }
}
