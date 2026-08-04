package net.shoreline.client.impl.command;

import net.shoreline.client.impl.command.abstracts.AbstractSocialCommand;
import net.shoreline.client.impl.social.SocialType;

public class FriendCommand extends AbstractSocialCommand
{
    public FriendCommand()
    {
        super(SocialType.FRIEND);
    }
}