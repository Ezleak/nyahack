package me.jiyun233.nya.notification

object NotificationManager {
    private val notifications = ArrayList<Notification>()
    private const val INTERVAL = 5f

    fun addNotification(notification: Notification) {
        notifications.add(notification.apply { y = calculateTotalHeight() })
    }

    fun draw() {
        val removeList = ArrayList<Notification>()

        for (notification in notifications) {
            if (notification.entering) {
                if (notification.animationXTo(notification.width)) {
                    notification.entering = false
                }
            }

            if (notification.exiting) {
                if (notification.animationXTo(0f)) {
                    removeList.add(notification)
                }
            }

            notification.draw()
        }

        rearrangeY()
        notifications.removeAll(removeList.toSet())
    }

    private fun rearrangeY() {
        var y = 0f
        notifications.forEach {
            y += INTERVAL
            y += it.height
            it.animationYTo(y)
        }
    }

    private fun calculateTotalHeight(): Float {
        var totalHeight = 0f

        notifications.forEach {
            totalHeight += it.height
            totalHeight += INTERVAL
        }

        return totalHeight
    }
}