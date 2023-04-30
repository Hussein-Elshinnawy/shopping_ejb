package shop;
//import javax.ejb.ActivationConfigProperty;
//import javax.ejb.MessageDriven;
//import javax.jms.JMSException;
//import javax.jms.MapMessage;
//import javax.jms.Message;
//import javax.jms.MessageListener;
import jakarta.ejb.MessageDriven;

import jakarta.jms.JMSException;
import jakarta.jms.MapMessage;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueConnection;
import javax.jms.QueueSession;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;

//@MessageDriven(name = "ShippingRequestNotificationMDB", activationConfig = {
//        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/ "),
//        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
//})
@MessageDriven(
        activationConfig = {@jakarta.ejb.ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue"),
                @jakarta.ejb.ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "java:/jms/queue/ShippingRequestNotificationQueue")
        },mappedName="java:/jms/queue/ShippingRequestNotificationQueue",name="ShippingRequestNotificationMDB"
)
public class ShippingRequestNotificationMDB implements MessageListener {

    @Override
    public void onMessage(Message message) {
        try {
            // extract the order and customer information from the message
            Long orderId = message.getLongProperty("orderId");
            String customerEmail = message.getStringProperty("customerEmail");
            String customerName = message.getStringProperty("customerName");

            // send a notification email to the customer
            String subject = "Shipping request processed for order #" + orderId;
            String body = "Dear " + customerName + ",\n\nYour shipping request for order #" + orderId + " has been processed. Thank you for choosing our shipping service.\n\nBest regards,\nThe Shipping Company";
            sendEmail(customerEmail, subject, body);
        } catch (JMSException e) {
            e.printStackTrace();
            // handle JMS exception
        }
    }

    private void sendEmail(String to, String subject, String body) {
        // send email using JavaMail API or any other email library
    }
}