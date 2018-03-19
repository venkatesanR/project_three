//Source file: E:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\dbutils\\DAOSQLLoaderTest.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.dbutils;

import java.util.Iterator;
import java.util.Hashtable;
import java.util.Vector;

/**
 * @author AddVal Technology Inc.
 */
public class DAOSQLLoaderTest {

	/**
	 * @param args[]
	 * @roseuid 3EA08D050370
	 */
	public static final void main(String args[]) {

		if (args.length < 2) {
			System.out.println( "Usage : com.addval.dbutils.DAOSQLLoaderTest DAORulesFile InputFile" );
			System.exit(1);
		}

			try {

				DAOSQLLoader loader = new DAOSQLLoader();
				DAOSQLStatement sqlStatement = null;
				Hashtable sqls = loader.loadSQL( args[0], args[1] );
/*
				Iterator iterator = sqls.keySet().iterator();

				while (iterator.hasNext()) {

					sqlStatement = (DAOSQLStatement)sqls.get( iterator.next() );

					System.out.println( sqlStatement );

					System.out.println( sqlStatement.getSQL( "SQLSERVER" ) );

				}
*/
				String defName = "RESPONSE";
				sqlStatement = (DAOSQLStatement)sqls.get(defName);
				DAOSQLLoaderTest tester = new DAOSQLLoaderTest();

				DAOTxtUtils utils = new DAOTxtUtils( sqlStatement, "ORACLE");
				utils.setProperties(tester.getResponse());
				String txt = utils.getText();
				System.out.println(txt);

			}
			catch (Exception e) {
				e.printStackTrace();
			}
	}

	public Response getResponse() {
		return new Response();
	}

	public class Response {

        private String messageType = null;
		private String version = null;
		private String localDate = null;
		private String localTmOfset = null;
		private String requestStn = null;
		private String userId = null;
		private String reqLNIATA = null;
        private Request request = null;
        private Vector itinOptions = null;

        public Response() {

            this.setMessageType("AVAILABILITY_RESPONSE");
            this.setVersion("10.01");
            this.setLocalDate("28MAY2003");
            this.setLocalTmOfset("120000+180");
            this.setRequestStn("EDI");
            this.setUserId("U123456");
            this.setReqLNIATA("1XXXXX");
            this.setRequest(new Request("BA","AAA",null));

            ItinOption itin = null;
            Vector segments = null;

            Vector itins = new Vector();

            //Record 1
            segments = new Vector();
            segments.add(new Segment("1001","SIN","SFO"));
            segments.add(new Segment("1002","SFO","BKK"));
            segments.add(new Segment("1003","BKK","IND"));
            itin = new ItinOption(segments,1);
            itins.add(itin);
            //Record 2
            segments = new Vector();
            segments.add(new Segment("2001","SIN","SFO"));
            segments.add(new Segment("2002","SFO","BKK"));
            itin = new ItinOption(segments,2);
            itins.add(itin);

            //Record 3
            segments = new Vector();
            segments.add(new Segment("3001","SIN","SFO"));
            segments.add(new Segment("3002","SFO","BKK"));
            itin = new ItinOption(segments,3);
            itins.add(itin);
            //Record 4
            segments = new Vector();
            segments.add(new Segment("4001","BKK","SIN"));
            segments.add(new Segment("4002","SIN","IND"));
            itin = new ItinOption(segments,4);
            itins.add(itin);

			//Record 5
			segments = new Vector();
			segments.add(new Segment("5001","BKK","SIN"));
			segments.add(new Segment("5002","SIN","IND"));
			segments.add(new Segment("5002","IND","LON"));
			itin = new ItinOption(segments,5);
			itins.add(itin);
            this.setItinOptions(itins);
		}
        public Request getRequest() {
            return request;
        }

        public void setRequest(Request request) {
            this.request = request;
        }
        public Vector getItinOptions() {
            return itinOptions;
        }

        public void setItinOptions(Vector itinOptions) {
            this.itinOptions = itinOptions;
        }
        public String getMessageType() {
            return messageType;
        }

        public void setMessageType(String messageType) {
            this.messageType = messageType;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getLocalDate() {
            return localDate;
        }

        public void setLocalDate(String localDate) {
            this.localDate = localDate;
        }

        public String getLocalTmOfset() {
            return localTmOfset;
        }

        public void setLocalTmOfset(String localTmOfset) {
            this.localTmOfset = localTmOfset;
        }

        public String getRequestStn() {
            return requestStn;
        }

        public void setRequestStn(String requestStn) {
            this.requestStn = requestStn;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getReqLNIATA() {
            return reqLNIATA;
        }

        public void setReqLNIATA(String reqLNIATA) {
            this.reqLNIATA = reqLNIATA;
        }
    }
        public class Request {
            private String carrierCode = null;
            private String origin = null;
            private String destination = null;
            public Request(String carrier,String origin,String destination){
                this.carrierCode = carrier;
                this.origin = origin;
                this.destination = destination;
            }
            public String getCarrierCode() {
                return carrierCode;
            }

            public void setCarrierCode(String carrierCode) {
                this.carrierCode = carrierCode;
            }

            public String getOrigin() {
                return origin;
            }

            public void setOrigin(String origin) {
                this.origin = origin;
            }

            public String getDestination() {
                return destination;
            }

            public void setDestination(String destination) {
                this.destination = destination;
            }
        }
        public class ItinOption{
            private int record = -1;
			private Vector segments = null;
			public ItinOption(Vector segments,int record) {
                this.segments = segments;
                this.record = record;
			}
            public int getRecord() {
                return record;
            }

            public void setRecord(int record) {
                this.record = record;
            }

            public Vector getSegments() {
                return segments;
            }

            public void setSegments(Vector segments) {
                this.segments = segments;
            }
		}
        public class Segment {
            private String fltNo = null;
            private String origin = null;
            private String destination = null;

            public Segment(String fltNo,String origin,String destination) {
                this.fltNo = fltNo;
                this.origin =  origin;
                this.destination = destination;
            }

            public String getFltNo() {
                return fltNo;
            }

            public void setFltNo(String fltNo) {
                this.fltNo = fltNo;
            }

            public String getOrigin() {
                return origin;
            }

            public void setOrigin(String origin) {
                this.origin = origin;
            }

            public String getDestination() {
                return destination;
            }

            public void setDestination(String destination) {
                this.destination = destination;
            }
        }

}

