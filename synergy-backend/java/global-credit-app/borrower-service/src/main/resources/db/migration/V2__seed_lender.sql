INSERT INTO lenders (id, slug, name, brand_color, active)
VALUES (
  '00000000-0000-0000-0000-000000000010',
  'abc-fintech',
  'ABC Fintech',
  '#3fb950',
  TRUE
)
ON CONFLICT (slug) DO NOTHING;
