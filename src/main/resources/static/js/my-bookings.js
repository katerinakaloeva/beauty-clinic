const loadingMessage = document.getElementById("loading-message");
const errorMessage = document.getElementById("error-message");
const emptyState = document.getElementById("empty-state");
const appointmentsContainer = document.getElementById("appointments");

const statusDetails = {
    CONFIRMED: {
        label: "Επιβεβαιωμένο",
        cssClass: "is-confirmed"
    },
    CANCELLED: {
        label: "Ακυρώθηκε",
        cssClass: "is-cancelled"
    },
    COMPLETED: {
        label: "Ολοκληρώθηκε",
        cssClass: "is-completed"
    },
    NO_SHOW: {
        label: "Δεν προσήλθες",
        cssClass: "is-no-show"
    }
};

function formatDate(date) {
    const [year, month, day] = date.split("-");

    return `${day}/${month}/${year}`;
}

function formatTime(time) {
    return time.substring(0, 5);
}

function createElement(tagName, className, text) {
    const element = document.createElement(tagName);

    if (className) {
        element.className = className;
    }

    if (text) {
        element.textContent = text;
    }

    return element;
}

function showError(message) {
    errorMessage.textContent = message;
    errorMessage.hidden = false;
}

function clearError() {
    errorMessage.textContent = "";
    errorMessage.hidden = true;
}

function getCsrfToken() {
    const csrfCookie = document.cookie
        .split("; ")
        .find(cookie => cookie.startsWith("XSRF-TOKEN="));

    return csrfCookie?.substring("XSRF-TOKEN=".length);
}

async function getErrorMessage(response, fallbackMessage) {
    if (response.status === 403) {
        return "Η ενέργεια απορρίφθηκε για λόγους ασφαλείας. Κάνε ανανέωση και ξαναπροσπάθησε.";
    }

    try {
        const errorResponse = await response.json();
        return errorResponse.message ?? fallbackMessage;
    } catch {
        return fallbackMessage;
    }
}

function isCancellable(appointment) {
    if (appointment.status !== "CONFIRMED") {
        return false;
    }

    const appointmentDateTime = new Date(
        `${appointment.appointmentDate}T${appointment.startTime}`
    );

    return appointmentDateTime > new Date();
}

function createAppointmentCard(appointment) {
    const card = createElement("article", "appointment-item");
    const icon = createElement("div", "appointment-icon", "♡");
    const details = createElement("div", "appointment-details");
    const treatmentName = createElement(
        "p",
        "treatment-name",
        appointment.treatmentName
    );
    const appointmentDate = createElement(
        "p",
        "appointment-date",
        `${formatDate(appointment.appointmentDate)} • ${formatTime(appointment.startTime)} – ${formatTime(appointment.endTime)}`
    );
    const statusAndAction = createElement("div", "status-and-action");
    const status = statusDetails[appointment.status] ?? {
        label: appointment.status,
        cssClass: ""
    };
    const statusBadge = createElement(
        "span",
        `status-badge ${status.cssClass}`,
        status.label
    );

    details.append(treatmentName, appointmentDate);
    statusAndAction.appendChild(statusBadge);
    card.append(icon, details, statusAndAction);

    if (isCancellable(appointment)) {
        const cancelButton = createElement(
            "button",
            "cancel-button",
            "Ακύρωση"
        );

        cancelButton.type = "button";
        cancelButton.addEventListener("click", () =>
            cancelAppointment(appointment.id, cancelButton)
        );

        statusAndAction.appendChild(cancelButton);
    }

    return card;
}

function renderAppointments(appointments) {
    appointmentsContainer.replaceChildren();

    appointments.forEach(appointment => {
        appointmentsContainer.appendChild(
            createAppointmentCard(appointment)
        );
    });
}

async function loadMyBookings() {
    loadingMessage.hidden = false;
    emptyState.hidden = true;
    clearError();

    try {
        const response = await fetch("/api/bookings/my", {
            credentials: "same-origin"
        });

        if (!response.ok) {
            throw new Error(
                await getErrorMessage(
                    response,
                    "Δεν ήταν δυνατή η φόρτωση των ραντεβού."
                )
            );
        }

        const appointments = await response.json();

        if (appointments.length === 0) {
            appointmentsContainer.replaceChildren();
            emptyState.hidden = false;
            return;
        }

        renderAppointments(appointments);
    } catch (error) {
        appointmentsContainer.replaceChildren();
        showError(error.message);
    } finally {
        loadingMessage.hidden = true;
    }
}

async function cancelAppointment(appointmentId, cancelButton) {
    const confirmed = window.confirm(
        "Θέλεις σίγουρα να ακυρώσεις αυτό το ραντεβού;"
    );

    if (!confirmed) {
        return;
    }

    const csrfToken = getCsrfToken();

    if (!csrfToken) {
        showError("Δεν βρέθηκε token ασφαλείας. Κάνε ανανέωση ή ξανασυνδέσου.");
        return;
    }

    cancelButton.disabled = true;
    cancelButton.textContent = "Ακύρωση...";
    clearError();

    try {
        const response = await fetch(
            `/api/bookings/${appointmentId}/cancel`,
            {
                method: "PATCH",
                credentials: "same-origin",
                headers: {
                    "X-XSRF-TOKEN": csrfToken
                }
            }
        );

        if (!response.ok) {
            throw new Error(
                await getErrorMessage(
                    response,
                    "Δεν ήταν δυνατή η ακύρωση του ραντεβού."
                )
            );
        }

        await loadMyBookings();
    } catch (error) {
        showError(error.message);
        cancelButton.disabled = false;
        cancelButton.textContent = "Ακύρωση";
    }
}

loadMyBookings();
