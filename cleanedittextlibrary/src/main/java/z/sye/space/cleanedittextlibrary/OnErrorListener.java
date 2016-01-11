package z.sye.space.cleanedittextlibrary;

/**
 * Created by Syehunter on 16/1/11.
 */
public interface OnErrorListener {

    /**
     * Return true if u want to show the errorMsg otherwise false
     *
     * This Method calls on when [onTextChange] and [lostFocus]
     *
     * @param input
     * @return
     */
    boolean onError(CharSequence input);
}
