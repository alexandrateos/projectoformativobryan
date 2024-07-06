Script base de datos:

CREATE TABLE Pacientes (
    UUID_paciente varchar2(50) primary key,
    nombre varchar2(50),
    apellido varchar2(50),
    edad varchar2(50),
    enfermedad varchar2(50),
    numeroHabitacion varchar2(50),
    numeroCama varchar2(50)
);

CREATE TABLE Medicamentos (
    UUID_medicamentos varchar2(50) primary key,
    medicamentosAsignados varchar2(50)
);

CREATE TABLE PacienteMedicamento (
    UUID_paciente varchar2(50),
    UUID_medicamentos varchar2(50),
    horaAplicacion varchar2(50),

    constraint fk_paciente
    foreign key (UUID_paciente)
    references tbPacientes (UUID_paciente),

    constraint fk_medicamentos
    foreign key (UUID_medicamentos)
    references tbMedicamentos (UUID_medicamentos)
);
