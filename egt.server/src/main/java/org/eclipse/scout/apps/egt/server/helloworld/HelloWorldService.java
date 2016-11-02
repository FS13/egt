package org.eclipse.scout.apps.egt.server.helloworld;

import org.eclipse.scout.apps.egt.server.ServerSession;
import org.eclipse.scout.apps.egt.shared.helloworld.HelloWorldFormData;
import org.eclipse.scout.apps.egt.shared.helloworld.IHelloWorldService;

/**
 * <h3>{@link HelloWorldService}</h3>
 *
 * @author Fritz Schinkel
 */
public class HelloWorldService implements IHelloWorldService {

	@Override
	public HelloWorldFormData load(HelloWorldFormData input) {
		StringBuilder msg = new StringBuilder();
		msg.append("Hello ").append(ServerSession.get().getUserId()).append('!');
		input.getMessage().setValue(msg.toString());
		return input;
	}
}
