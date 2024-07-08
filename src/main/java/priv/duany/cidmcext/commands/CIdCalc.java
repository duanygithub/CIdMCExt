package priv.duany.cidmcext.commands;

import net.duany.ciCore.CInterpreter;
import net.duany.ciCore.exception.CIdGrammarException;
import net.duany.ciCore.gramma.BlockTreeNode;
import net.duany.ciCore.gramma.GrammarProc;
import net.duany.ciCore.gramma.RootTreeNode;
import net.duany.ciCore.gramma.TreeNode;
import net.duany.ciCore.variable.Variable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CIdCalc extends CommandBase {

    @Override
    public String getName() {
        return "cidcalc";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "command.cidcalc.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException{
        StringBuilder sb = new StringBuilder();
        if(args.length == 0) {
            return;
        }
        for(String str : args) {
            sb.append(str).append(" ");
        }
        sb.delete(sb.length() - 1, sb.length());
        sender.sendMessage(new TextComponentString("执行代码: " + sb.toString()));
        try {
            Variable res = calc(sb.toString());
            sender.sendMessage(new TextComponentString(res.toString()));
        } catch (CIdGrammarException e) {
            throw new RuntimeException(e);
        }
    }

    private Variable calc(String exp) throws CIdGrammarException {
        CInterpreter cInterpreter = new CInterpreter(exp, false);
        GrammarProc gp = new GrammarProc();
        gp.preProcess(exp);
        gp.root = new RootTreeNode(0, gp.codeBlocks.size(), null);
        BlockTreeNode block = new BlockTreeNode(gp.root.lIndex, gp.root.rIndex, gp.root);
        gp.buildTree(block);
        cInterpreter.setGrammarProc(gp);
        return cInterpreter.execBlock(block);
    }
}
