package hello.springtx.order;

public class NotEnoughMoneyException extends Exception{

    //비즈니스 예외를 체크예외로 만드는 이유는 데이터를 남기기 위해서(rollback을 하면 안 됨.)
    public NotEnoughMoneyException(String message) {
        super(message);
    }



}
