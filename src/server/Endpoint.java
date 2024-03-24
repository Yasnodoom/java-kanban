package server;

public enum Endpoint {
    GET_TASKS, GET_TASK_ID, POST_TASKS, DELETE_TASKS,
    GET_SUBTASKS, GET_SUBTASKS_ID, POST_SUBTASKS, DELETE_SUBTASKS,
    GET_EPICS, GET_EPIC_ID, GET_EPIC_SUBTASKS, POST_EPICS, DELETE_EPICS,
    GET_HISTORY, GET_PRIORITIZED, UNKNOWN
}
