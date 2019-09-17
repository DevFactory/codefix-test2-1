import { Branch } from './branch';

export class Repo {

  id: number;
  branch: string;
  url: string;
  active: boolean;
  branches: Branch[];

  constructor(id: number, branch: string, url: string, active: boolean) {
    this.id = id;
    this.branch = branch;
    this.url = url;
    this.active = active;
    this.branches = [new Branch(this.branch)];
  }

  withActive(activate: boolean): Repo {
    const newRepo: Repo = new Repo(this.id, this.branch, this.url, this.active);
    newRepo.active = activate;
    return newRepo;
  }
}
