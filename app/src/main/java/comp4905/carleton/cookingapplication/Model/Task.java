package comp4905.carleton.cookingapplication.Model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;
import io.realm.annotations.Required;
import org.bson.types.ObjectId;

import javax.annotation.Nonnull;

public class Task extends RealmObject {
    @PrimaryKey
    @Required
    @Nonnull
    private ObjectId _id = new ObjectId();
    @Required
    private String name = "Task";
    @Required
    private String status = TaskStatus.Open.name();
    public void setStatus(TaskStatus status) {
        this.status = status.name();
    }
    public String getStatus() {
        return this.status;
    }
    public ObjectId get_id() {
        return _id;
    }
    public void set_id(ObjectId _id) {
        this._id = _id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Task(String _name) {
        this.name = _name;
    }
    public Task() {}
}