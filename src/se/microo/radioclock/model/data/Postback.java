package se.microo.radioclock.model.data;

/**
 * Interface between two classes in a relationship.
 * One of the classes acts a child of the other class.
 * The child performs a specific task which must be
 * finished before the parent can continue.
 * 
 * The parent is called a Postback, and when the child
 * is finishes it calls the finished()-method to
 * notify the Postback that it can resume with
 * remaining tasks.
 * 
 * @author Micro
 */
public interface Postback {
	
	/**
	 * Called when a child class finishes its task.
	 */
	public void finished();
}
