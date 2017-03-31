package com.tdcc.mq;

import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.CMQC;

public class MQManagerReceiver {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		if (args.length != 2) {
			
			System.out.println("Usage : java MQManagerReceive [hostname] [queuename]");
			System.exit(-1);
		}
		
		MQQueueManager queueManager = null;
		MQQueue queue = null;
		
		try {

			MQEnvironment.hostname = args[0];
			MQEnvironment.channel = "MPSBK.SVRCONN";
			MQEnvironment.userID = "mqm";

			queueManager = new MQQueueManager("MOBILE.SERVICE.QM");

			int openOptions = CMQC.MQOO_INPUT_AS_Q_DEF | CMQC.MQOO_OUTPUT | CMQC.MQOO_INQUIRE;

			// MQQueue queue =
			// queueManager.accessQueue("PUSHSEND.IN.LOCAL.QUEUE", openOptions);
			queue = queueManager.accessQueue(args[1], openOptions);

			int depth = queue.getCurrentDepth();
			System.out.println("Current depth: " + depth + "\n");
			if (depth == 0) System.exit(0);
			
			MQGetMessageOptions gmo = new MQGetMessageOptions();
			gmo.options = CMQC.MQGMO_NO_WAIT + CMQC.MQGMO_FAIL_IF_QUIESCING + CMQC.MQGMO_CONVERT;

			while(true) {
				
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
