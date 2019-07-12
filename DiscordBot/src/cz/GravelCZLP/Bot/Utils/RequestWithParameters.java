package cz.GravelCZLP.Bot.Utils;

import sx.blah.discord.util.RequestBuffer.IRequest;

public abstract class RequestWithParameters<T, S> implements IRequest<T>{
	
	public S s;
	
	public RequestWithParameters(S s) {
		this.s = s;
	}
	
}
