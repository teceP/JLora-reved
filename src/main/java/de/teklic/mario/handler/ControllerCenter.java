package de.teklic.mario.handler;
/*
 *
 * @author Mario Teklic
 */


public class ControllerCenter {
   /* private de.mario.teklic.jlora.controller.AcknowledgeController acknowledgeController;
    private ErrorHandler errorController;
    private Handler messageController;
    private de.mario.teklic.jlora.controller.ReplyHandler replyController;
    private RequestHandler requestController;
    private de.mario.teklic.jlora.controller.UnreachableHandler unreachableController;

    public ControllerCenter(){
        this.acknowledgeController = new de.mario.teklic.jlora.controller.AcknowledgeController();
        this.errorController = new ErrorHandler();
        this.messageController = new Handler();
        this.replyController = new de.mario.teklic.jlora.controller.ReplyHandler();
        this.requestController = new RequestHandler();
        this.unreachableController = new de.mario.teklic.jlora.controller.UnreachableHandler();
    }

    public void process(String msg){
        RouteX routeX = RouteCreater.tokenize(msg);
        System.out.println((routeX.getFlag() == RouteFlag.NO_ROUTEX ? "No RouteX." : "Analyzed Message: " + routeX));

        switch (routeX.getFlag()) {
            case ERROR:
                errorController.incoming((RouteX.RouteError) routeX);
                break;
            case REPLY:
                replyController.incoming((RouteX.RouteReply) routeX);
                break;
            case REQUEST:
                requestController.incoming((RouteX.RouteRequest) routeX);
                break;
            case UNREACHABLE:
                unreachableController.incoming((RouteX.RouteUnreachable) routeX);
                break;
            case NO_ROUTEX:
                break;
            case MESSAGE:
                messageController.incoming((RouteX.Message) routeX);
                break;
            case ACKNOWLEDGE:
                acknowledgeController.incoming((RouteX.Acknowledge) routeX);
                break;
            default:
                System.out.println("Could not determine RouteFlag of RouteX instance.");
        }
        System.out.println("======================================================");
    }*/
}
