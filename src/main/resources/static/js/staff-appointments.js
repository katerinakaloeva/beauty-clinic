const loadingMessage = document.getElementById("loading-message");
const errorMessage = document.getElementById("error-message");
const emptyState = document.getElementById("empty-state");
const appointmentsContainer = document.getElementById("appointments");

function formatDate(date) {
    const [year, month, day] = date.split("-");
    return `${day}/${month}/${year}`;
}

function formatTime(time) {
    return time.substring(0, 5);
}

function getStatusDetails(status) {
    if (status === "CONFIRMED") {
        return { label: "Επιβεβαιωμένο", className: "is-confirmed" };
    }

    if (status === "CANCELLED") {
        return { label: "Ακυρωμένο", className: "is-cancelled" };
    }

    if (status === "COMPLETED") {
        return { label: "Ολοκληρωμένο", className: "is-completed" };
    }

    if (status === "NO_SHOW") {
        return { label: "Δεν προσήλθε", className: "is-no-show" };
    }

    return { label: status, className: "" };
}

function createAppointmentCard(appointment) {
    const status = getStatusDetails(appointment.status);

    const card = document.createElement("article");
    card.className = "appointment-item";

    const icon = document.createElement("div");
    icon.className = "appointment-icon";
    icon.setAttribute("aria-hidden", "true");
    icon.textContent = "✦";

    const details = document.createElement("div");
    details.className = "appointment-details";

    const treatmentName = document.createElement("p");
    treatmentName.className = "treatment-name";
    treatmentName.textContent = appointment.treatmentName;

    const appointmentDate = document.createElement("p");
    appointmentDate.className = "appointment-date";
    appointmentDate.textContent = formatDate(appointment.appointmentDate);

    const appointmentTime = document.createElement("p");
    appointmentTime.className = "appointment-time";
    appointmentTime.textContent = `${formatTime(appointment.startTime)} – ${formatTime(appointment.endTime)}`;

    details.append(treatmentName, appointmentDate, appointmentTime);

    const statusBadge = document.createElement("span");
    statusBadge.className = `status-badge ${status.className}`;
    statusBadge.textContent = status.label;

    card.append(icon, details, statusBadge);

    return card;
}

async function loadAppointments() {
    try {
        const response = await fetch("/api/staff/appointments");

        if (!response.ok) {
            throw new Error("Δεν ήταν δυνατή η φόρτωση των ραντεβού.");
        }

        const appointments = await response.json();

        loadingMessage.hidden = true;

        if (appointments.length === 0) {
            emptyState.hidden = false;
            return;
        }

        appointmentsContainer.textContent = "";

        for (const appointment of appointments) {
            const card = createAppointmentCard(appointment);
            appointmentsContainer.appendChild(card);
        }
    } catch (error) {
        loadingMessage.hidden = true;
        errorMessage.textContent = error.message;
        errorMessage.hidden = false;
    }
}

loadAppointments();
