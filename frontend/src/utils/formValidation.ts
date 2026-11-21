type ValidatableField = HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement;

export const getFormValidationError = (form: HTMLFormElement): string | null => {
  const invalidField = form.querySelector<ValidatableField>(':invalid');

  if (invalidField) {
    invalidField.focus();

    if (invalidField instanceof HTMLInputElement && invalidField.validity.typeMismatch && invalidField.type === 'email') {
      return 'Informe um endereço de e-mail válido.';
    }
    if (invalidField.validity.valueMissing) {
      return 'Preencha todos os campos obrigatórios.';
    }
    if (invalidField.validity.tooShort) {
      return 'Este campo deve conter pelo menos 3 caracteres.';
    }
    if (invalidField.validity.rangeUnderflow || invalidField.validity.rangeOverflow) {
      return 'Informe um valor dentro do intervalo permitido.';
    }
    if (invalidField.validity.stepMismatch) {
      return 'Informe um valor válido.';
    }

    return 'Verifique o formato dos dados informados.';
  }

  const fieldWithoutLetter = Array.from(
    form.querySelectorAll<HTMLInputElement | HTMLTextAreaElement>('[data-requires-letter]')
  ).find((field) => !/\p{L}/u.test(field.value));

  if (fieldWithoutLetter) {
    fieldWithoutLetter.focus();
    return 'Este campo deve conter pelo menos uma letra.';
  }

  return null;
};
