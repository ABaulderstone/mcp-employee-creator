export const GLOSSARY: Record<string, string> = {
  promotion:
    'A change in job title or salary increase for an employee, typically reflected in a new contract entry',
  'active contract':
    "A contract where is_active = 1, representing the employee's current employment terms",
  tenure:
    'The length of time an employee has been with the company, calculated from their contract with the earliest start_date',
  department:
    'An organizational unit within the company, referenced by department_id in contracts',
  salary:
    'The compensation amount for an employee, stored in the contracts table',
  employee:
    'A person working for the company, with personal details stored in the employees table',
  contract:
    'An employment agreement linking an employee to a department with specific terms (job title, salary, dates)',
  start_date: 'The date when a contract begins',
  end_date: 'The date when a contract ends (NULL for ongoing contracts)',
  job_title:
    'The position or role held by an employee under a specific contract',
};
