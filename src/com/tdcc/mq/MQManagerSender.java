package com.tdcc.mq;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;

import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQPutMessageOptions;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.CMQC;

public class MQManagerSender {

	public static void main(String[] args) {
		
		if (args.length != 3) {
			
			System.out.println("Usage : java MQManagerSender [hostname] [queuename] [inputfile]");
			System.exit(-1);
		}

		BufferedReader br = null;
		FileReader fr = null;		
		MQQueueManager queueManager = null;
		
		try {
				
			/*
			br = new BufferedReader(new InputStreamReader(
			                      new FileInputStream(args[2]), "UTF-8"));
			String msg1 = "";
			while ((msg1 = br.readLine()) != null) {
				
				System.out.println("Send message : " + msg1);
			}
			*/			
			
			MQEnvironment.hostname = args[0];
			MQEnvironment.channel = "MPSBK.SVRCONN";
			MQEnvironment.userID = "mqm";

			queueManager = new MQQueueManager("MOBILE.SERVICE.QM");

			int openOptions = CMQC.MQOO_INPUT_AS_Q_DEF | CMQC.MQOO_OUTPUT;

			// MQQueue queue =
			// queueManager.accessQueue("PUSHSEND.IN.LOCAL.QUEUE", openOptions);
			MQQueue queue = queueManager.accessQueue(args[1], openOptions);
			
			MQPutMessageOptions pmo = new MQPutMessageOptions();
			
			br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(args[2]), "UTF-8"));
			
			String msg = "";
			MQMessage sendMessage = new MQMessage();
			
			while ((msg = br.readLine()) != null) {
				
				System.out.println("Send message : " + msg);
				
				sendMessage.messageId = CMQC.MQMI_NONE;
				sendMessage.correlationId = CMQC.MQCI_NONE;
				sendMessage.format = CMQC.MQFMT_STRING;
				sendMessage.feedback = CMQC.MQFB_NONE;
				sendMessage.messageType = CMQC.MQMT_DATAGRAM;
				sendMessage.characterSet = 1208;
				sendMessage.writeString(msg);
								
				queue.put(sendMessage, pmo);
				
				sendMessage.clearMessage();
			}
			
			queue.close();
		} catch (Exception ex) {

			ex.printStackTrace();
		} finally {

			try {
				
				if (br != null)
					br.close();

				if (fr != null)
					fr.close();
				
				if (queueManager != null)
					queueManager.disconnect();

			} catch (Exception ex) {

				ex.printStackTrace();
			}
		}
	}

}
