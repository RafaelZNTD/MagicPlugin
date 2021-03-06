package com.elmakers.mine.bukkit.action.builtin;

import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.elmakers.mine.bukkit.action.BaseSpellAction;
import com.elmakers.mine.bukkit.api.action.CastContext;
import com.elmakers.mine.bukkit.api.spell.SpellResult;
import com.elmakers.mine.bukkit.boss.BossBarConfiguration;

public class BossBarAction extends BaseSpellAction {
    private BossBarConfiguration barConfig;
    private boolean showTarget;

    private double progress;
    private boolean updateTitle;

    // Transient state
    private BossBar bossBar;

    @Override
    public void prepare(CastContext context, ConfigurationSection parameters) {
        super.prepare(context, parameters);

        updateTitle = parameters.getBoolean("update_title");
        showTarget = parameters.getBoolean("show_target");
        progress = parameters.getDouble("bar_progress");
        barConfig = new BossBarConfiguration(context.getController(), parameters);
    }

    @Override
    public SpellResult perform(CastContext context) {
        if (bossBar == null) {
            bossBar = barConfig.createBossBar(context);
        }
        if (updateTitle) {
            barConfig.updateTitle(bossBar, context);
        }
        bossBar.setProgress(Math.max(0, Math.min(1, progress)));

        Entity targetEntity = showTarget ? context.getTargetEntity() : context.getEntity();
        if (targetEntity == null) {
            return SpellResult.NO_TARGET;
        }
        if (!(targetEntity instanceof Player)) {
            return SpellResult.PLAYER_REQUIRED;
        }

        bossBar.addPlayer((Player)targetEntity);
        return SpellResult.NO_ACTION;
    }

    @Override
    public void finish(CastContext context) {
        super.finish(context);
        if (bossBar != null) {
            bossBar.setVisible(false);
            bossBar.removeAll();
            bossBar = null;
        }
    }
}
