package com.milan.todolist.todo;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.transaction.Transactional;

@RestController
public class TodoController {
    @Autowired
    private TodoRepository repo;

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/getAllTask")
    public List<Todo> getAllTasks() {
        List<Todo> res = repo.findAll();
        return res;
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/getOneTask/{id}")
    public Todo getTaskById(@PathVariable Long id) {
        Optional<Todo> tsk = repo.findById(id);
        if (tsk.isPresent()) {
            return tsk.get();
        } else {
            throw new IllegalArgumentException("Task With Id Does not exists");
        }
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @RequestMapping(method = RequestMethod.POST, path = "/createTask")
    public Todo addTask(@RequestBody Map<String, String> body) {
        Todo task = new Todo();
        task.setTopic(body.get("topic"));
        task.setDescription(body.get("description"));
        task.setCreatedAt();
        task.setCompletionTime(body.get("completionTime"));
        task.setStatus("Pending");
        repo.save(task);
        return task;
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @Transactional
    @PutMapping("/updateTask/{id}")
    public Todo updateTask(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Todo task = repo.findById(id).orElseThrow(() -> new IllegalStateException("Task Not Found"));
        String topic = body.containsKey("topic") ? body.get("topic") : task.getStatus();
        String description = body.containsKey("description") ? body.get("desciption") : task.getDescription();
        String status = body.containsKey("status") ? body.get("status") : task.getStatus();
        String completionTime = body.containsKey("completionTime") ? body.get("completionTime") : null;

        if (completionTime != null
                && completionTime.length() > 0
                && !completionTime.equals(task.getCompletionTime())) {
            task.setCompletionTime(completionTime);
        }
        if (topic != null &&
                topic.length() > 0 &&
                !topic.equals(task.getTopic())) {
            task.setTopic(topic);
        }

        if (description != null &&
                description.length() > 0 &&
                !description.equals(task.getDescription())) {
            task.setDescription(description);
        }

        if (status != null &&
                status.length() > 0 &&
                !status.equals(task.getStatus())) {
            task.setStatus(status);
        }

        return task;
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @DeleteMapping("/deleteTask/{id}")
    public void deleteTask(@PathVariable Long id) {
        boolean b = repo.existsById(id);
        if (b) {
            repo.deleteById(id);
        } else {
            throw new IllegalArgumentException("Id Does Not Exists");
        }
    }
}
