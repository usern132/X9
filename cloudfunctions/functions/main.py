import firebase_admin
from firebase_functions.options import set_global_options
from firebase_admin import messaging
from firebase_functions import db_fn

firebase_admin.initialize_app()
set_global_options(max_instances=5, region="europe-west1")

NEW_REPORTS = "new_reports"


@db_fn.on_value_created(reference="reports/{reportId}")
def notify_new_report(event: db_fn.Event):
    report_data = event.data
    if not report_data:
        return

    message = messaging.Message(
        notification=messaging.Notification(
            title="New report",
            body=report_data.get("title"),
            image=report_data.get("remoteImageUri"),
        ),
        topic=NEW_REPORTS,
        android=messaging.AndroidConfig(
            notification=messaging.AndroidNotification(channel_id=NEW_REPORTS)
        ),
        data={
            "reportId": event.params["reportId"],
        },
    )

    try:
        response = messaging.send(message)
        print(f"Successfully sent message: {response}")
    except Exception as e:
        print(f"Error sending message: {e}")
