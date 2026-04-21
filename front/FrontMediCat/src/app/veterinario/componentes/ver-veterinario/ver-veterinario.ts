import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { VeterinarioMapper } from '../../../shared/api/model-mappers';
import { Navbar } from '../../../shared/components/navbar/navbar';
import { Veterinario } from '../../veterinario';
import { VeterinarioRestService } from '../../services/veterinario-rest.service';

@Component({
  selector: 'app-ver-veterinario',
  standalone: true,
  imports: [CommonModule, RouterLink, Navbar],
  templateUrl: './ver-veterinario.html',
  styleUrl: './ver-veterinario.css',
})
export class VerVeterinario implements OnInit {
  veterinario: Veterinario | null = null;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly veterinarioRestService: VeterinarioRestService,
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    this.veterinarioRestService.findById(id).subscribe({
      next: (vetDto) => {
        this.veterinario = VeterinarioMapper.fromDto(vetDto);
      },
      error: () => {
        this.veterinario = null;
      },
    });
  }
}