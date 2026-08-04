package net.shoreline.client.impl.command;

import net.shoreline.client.impl.command.abstracts.AbstractSocialCommand;
import net.shoreline.client.impl.social.SocialType;

public class EnemyCommand extends AbstractSocialCommand
{
    public EnemyCommand()
    {
        super(SocialType.ENEMY);
    }
}