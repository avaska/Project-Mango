package mango.payment.db;

import mango.connection.db.DBconnection;

public class PayMentDAO extends DBconnection implements IPayMent{

	
	
	
	
	
	@Override
	public int InsertPayment(PaymentBean pb) {
	
		try {
			getConnection();
			
			
			
			
			
		} catch (Exception e) {
		
			System.out.println("InsertPayment()메소드에서 오류발생"+e);
			
		}
		
		
		
		
		return 0;
	}

	
	
	
	
	
	

	
	
	
	
	
	
	
	
	
	
}
