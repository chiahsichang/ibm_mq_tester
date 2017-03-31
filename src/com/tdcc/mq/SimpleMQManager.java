package com.tdcc.mq;

import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQPutMessageOptions;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.CMQC;

public class SimpleMQManager {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		MQQueueManager queueManager = null;
		MQQueue queue = null;
		
		try {

			MQEnvironment.hostname = "127.0.0.1";
			MQEnvironment.channel = "DEV.ADMIN.SVRCONN";
			MQEnvironment.userID = "admin";
			MQEnvironment.password = "passw0rd";

			queueManager = new MQQueueManager("QM1");

			int openOptions = CMQC.MQOO_INPUT_AS_Q_DEF | CMQC.MQOO_OUTPUT | CMQC.MQOO_INQUIRE;

			// MQQueue queue =
			// queueManager.accessQueue("PUSHSEND.IN.LOCAL.QUEUE", openOptions);
			queue = queueManager.accessQueue("DEV.QUEUE.1", openOptions);

			MQMessage hello_world = new MQMessage();

			for (int index = 0; index < 10; index++) {
			
				hello_world.messageId = CMQC.MQMI_NONE;
				hello_world.correlationId = CMQC.MQCI_NONE;
				hello_world.format = CMQC.MQFMT_STRING;
				hello_world.feedback = CMQC.MQFB_NONE;
				hello_world.messageType = CMQC.MQMT_DATAGRAM;
				hello_world.characterSet = 1208;
				hello_world.writeString("Hello World! : " + index);
				
				MQPutMessageOptions pmo = new MQPutMessageOptions();
				queue.put(hello_world, pmo);
				
				hello_world.clearMessage();
			}

			int depth = queue.getCurrentDepth();
			System.out.println("Current depth: " + depth + "\n");
			if (depth == 0)
				System.exit(0);

			MQGetMessageOptions gmo = new MQGetMessageOptions();
			gmo.options = CMQC.MQGMO_NO_WAIT + CMQC.MQGMO_FAIL_IF_QUIESCING + CMQC.MQGMO_CONVERT;

			while (true) {

				MQMessage retrievedMessage = new MQMessage();

				queue.get(retrievedMessage, gmo);

				byte[] b = new byte[retrievedMessage.getMessageLength()];
				retrievedMessage.readFully(b);

				String msgText = new String(b);

				System.out.println(msgText);

				retrievedMessage.clearMessage();
			}
		} catch (Exception ex) {

			ex.printStackTrace();
		} finally {

			try {
				if (queue != null)
					queue.close();

				if (queueManager != null)
					queueManager.disconnect();

			} catch (Exception ex) {

				ex.printStackTrace();
			}
		}
	}

}
