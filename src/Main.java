import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.logging.Level.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static final String AUSTIN_POWERS = "Austin Powers";
    public static final String WEAPONS = "weapons";
    public static final String BANNED_SUBSTANCE = "banned substance";
    public static interface Sendable {
        String getFrom();
        String getTo();
    }

    public static abstract class AbstractSendable implements Sendable {

        protected final String from;
        protected final String to;

        public AbstractSendable(String from, String to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public String getFrom() {
            return from;
        }

        @Override
        public String getTo() {
            return to;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            AbstractSendable that = (AbstractSendable) o;

            if (!from.equals(that.from)) return false;
            if (!to.equals(that.to)) return false;

            return true;
        }

    }

    public static class MailMessage extends AbstractSendable {

        private final String message;

        public MailMessage(String from, String to, String message) {
            super(from, to);
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            MailMessage that = (MailMessage) o;

            if (message != null ? !message.equals(that.message) : that.message != null) return false;

            return true;
        }

    }

    public static class MailPackage extends AbstractSendable {
        private final Package content;

        public MailPackage(String from, String to, Package content) {
            super(from, to);
            this.content = content;
        }

        public Package getContent() {
            return content;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            MailPackage that = (MailPackage) o;

            if (!content.equals(that.content)) return false;

            return true;
        }

    }

    public static class Package {
        private final String content;
        private final int price;

        public Package(String content, int price) {
            this.content = content;
            this.price = price;
        }

        public String getContent() {
            return content;
        }

        public int getPrice() {
            return price;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Package aPackage = (Package) o;

            if (price != aPackage.price) return false;
            if (!content.equals(aPackage.content)) return false;

            return true;
        }
    }

    public static interface MailService {
        Sendable processMail(Sendable mail);
    }

    /*
    Класс, в котором скрыта логика настоящей почты
    */
    public static class RealMailService implements MailService {

        @Override
        public Sendable processMail(Sendable mail) {
            // Здесь описан код настоящей системы отправки почты.
            return mail;
        }
    }

    public static class UntrustworthyMailWorker implements MailService {
        private static final RealMailService realWorker = new RealMailService();
        private static MailService[] workers;
        public UntrustworthyMailWorker(MailService[] w) {
            UntrustworthyMailWorker.workers = w;

        }

        public RealMailService getRealMailService() {
            return realWorker;
        }

        @Override
        public Sendable processMail(Sendable mail) {
            Sendable proceed = mail;
            for (MailService worker : workers) {
                proceed = worker.processMail(mail);
            }
            return realWorker.processMail(proceed);
        }
    }

    public static class Spy implements MailService {
        private final Logger logger;
        public Spy(Logger logger) {
            this.logger = logger;
        }

        @Override
        public Sendable processMail(Sendable mail) {
            if (mail instanceof MailMessage) {
                if (mail.getFrom().equals(AUSTIN_POWERS) || mail.getTo().equals(AUSTIN_POWERS)) {
                    logger.log(WARNING, "Detected target mail correspondence: from {from} to {to} \"{message}\"", new Object[]{mail.getFrom(), mail.getTo(), ((MailMessage) mail).getMessage()});
                }
                else logger.log(INFO, "Usual correspondence: from {from} to {to}", new Object[]{mail.getFrom(), mail.getTo()});
            }
            return mail;
        }
    }

    public static class Thief implements MailService {
        private static int minValue;
        private static int stolenValue;
        public Thief(int minValue) {
            Thief.minValue = minValue;
        }

        public int getStolenValue() {
            return Thief.stolenValue;
        }
        @Override
        public Sendable processMail(Sendable mail) {
            if (mail instanceof MailPackage) {
                Package pac = ((MailPackage) mail).getContent();
                MailPackage fakeMail = new MailPackage(mail.getFrom(), mail.getTo(), new Package("stones instead of " + pac.getContent(), 0));
                Thief.stolenValue += pac.getPrice();
                return fakeMail;
            }
            return mail;
        }
    }

    public static class IllegalPackageException extends RuntimeException {}


    public static class StolenPackageException extends RuntimeException {}

    public static class Inspector implements  MailService {
        @Override
        public Sendable processMail(Sendable mail) {
            if (mail instanceof MailPackage) {
                Package pac = ((MailPackage) mail).getContent();
                String content = pac.getContent();
                try {
                    if (content.equals(WEAPONS) || content.equals(BANNED_SUBSTANCE)) {
                        throw new IllegalPackageException();
                    }
                    else if (content.toLowerCase().contains("stone")) {
                        throw new StolenPackageException();
                    }
                }
                catch (IllegalPackageException | StolenPackageException e) {
                    System.out.println(e.getMessage());
                }
            }
            return mail;
        }
    }

    public static void main(String[] args) {

    }
}