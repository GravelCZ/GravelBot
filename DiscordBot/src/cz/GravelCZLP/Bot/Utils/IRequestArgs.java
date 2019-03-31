package cz.GravelCZLP.Bot.Utils;

import sx.blah.discord.util.RequestBuffer.IRequest;

public abstract class IRequestArgs<T, S> implements IRequest<T>{
	
	public S s;
	
	public IRequestArgs(S s) {
		this.s = s;
	}
	
}
